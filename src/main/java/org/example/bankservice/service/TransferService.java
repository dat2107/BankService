package org.example.bankservice.service;

import lombok.RequiredArgsConstructor;
import org.example.bankservice.dto.TransferDTO;
import org.example.bankservice.dto.OtpConfirmDTO;
import org.example.bankservice.model.*;
import org.example.bankservice.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class TransferService {

    private final TransactionRepository transactionRepo;
    private final OtpTransactionRepository otpRepo;
    private final CardRepository cardRepo;
    private final EmailService emailService;

    /**
     * Bước 1: Tạo giao dịch và sinh OTP
     */
    @Transactional
    public Transaction createTransferRequest(TransferDTO dto) {
        Card fromCard = cardRepo.findById(dto.getFromCardId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy thẻ nguồn"));

        Card toCard = cardRepo.findByCardNumber(dto.getToCardNumber())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy thẻ nhận"));

        if (dto.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Số tiền không hợp lệ");
        }

        if (fromCard.getAccount().getBalance().getAvailableBalance().compareTo(dto.getAmount()) < 0) {
            throw new RuntimeException("Số dư không đủ");
        }

        // Tạo transaction
        Transaction tx = Transaction.builder()
                .fromCard(fromCard)
                .toCard(toCard)
                .amount(dto.getAmount())
                .status(Transaction.TransactionStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .build();
        transactionRepo.save(tx);

        // Sinh OTP
        String otp = String.valueOf(100000 + new Random().nextInt(900000));
        OtpTransaction otpTx = OtpTransaction.builder()
                .transaction(tx)
                .otpCode(otp)
                .expireAt(LocalDateTime.now().plusMinutes(5))
                .verified(false)
                .build();
        otpRepo.save(otpTx);

        // Gửi OTP qua email
        emailService.sendEmail(dto.getEmail(),
                "Mã OTP xác nhận giao dịch",
                "<p>Mã OTP của bạn là: <b>" + otp + "</b> (hiệu lực 5 phút).</p>");

        return tx;
    }

    /**
     * Bước 2: Xác nhận OTP và thực hiện giao dịch
     */
    @Transactional
    public Transaction confirmOtp(OtpConfirmDTO dto) {
        OtpTransaction otpTx = otpRepo.findByTransaction_TransactionId(dto.getTransactionId());
        if (otpTx == null) throw new RuntimeException("Không tìm thấy OTP");

        if (otpTx.isVerified() || otpTx.getExpireAt().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("OTP hết hạn hoặc đã được dùng");
        }
        if (!otpTx.getOtpCode().equals(dto.getOtp())) {
            throw new RuntimeException("OTP không đúng");
        }

        System.out.println("OTP trong DB: " + otpTx.getOtpCode());
        System.out.println("expireAt: " + otpTx.getExpireAt());
        System.out.println("now: " + LocalDateTime.now());
        System.out.println("verified: " + otpTx.isVerified());


        otpTx.setVerified(true);
        otpRepo.save(otpTx);

        Transaction tx = otpTx.getTransaction();
        Card fromCard = tx.getFromCard();
        Card toCard = tx.getToCard();
        BigDecimal amount = tx.getAmount();



        // Cập nhật balance
        fromCard.getAccount().getBalance().setAvailableBalance(
                fromCard.getAccount().getBalance().getAvailableBalance().subtract(amount)
        );
        toCard.getAccount().getBalance().setAvailableBalance(
                toCard.getAccount().getBalance().getAvailableBalance().add(amount)
        );

        tx.setStatus(Transaction.TransactionStatus.SUCCESS);

        return transactionRepo.save(tx);
    }
}
