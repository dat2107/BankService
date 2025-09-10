package org.example.bankservice.service;

import lombok.RequiredArgsConstructor;
import org.example.bankservice.dto.PaymentRequest;
import org.example.bankservice.dto.TransferDTO;
import org.example.bankservice.dto.OtpConfirmDTO;
import org.example.bankservice.model.*;
import org.example.bankservice.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

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
    private final RestTemplate restTemplate = new RestTemplate();
    @Autowired
    private BalanceRepository balanceRepository;
    @Autowired
    private AccountRepository accountRepository;

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
                .type(Transaction.TransactionType.TRANSFER)
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
     * Bước 2: Xác nhận OTP -> chuyển trạng thái sang WAITING_APPROVAL
     * và chuyển tiền từ availableBalance -> holdBalance (người gửi)
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
        if (tx.getStatus() != Transaction.TransactionStatus.PENDING) {
            throw new RuntimeException("Trạng thái giao dịch không hợp lệ");
        }
        Card fromCard = tx.getFromCard();
        BigDecimal amount = tx.getAmount();

        // Trừ availableBalance người gửi
        fromCard.getAccount().getBalance().setAvailableBalance(
                fromCard.getAccount().getBalance().getAvailableBalance().subtract(amount)
        );

        // Cộng vào holdBalance người gửi
        fromCard.getAccount().getBalance().setHoldBalance(
                fromCard.getAccount().getBalance().getHoldBalance().add(amount)
        );

        tx.setStatus(Transaction.TransactionStatus.WAITING_APPROVAL);
        transactionRepo.save(tx);

        return tx;
    }

    /**
     * Bước 3a: Admin duyệt giao dịch
     */
    @Transactional
    public Transaction approveTransaction(Long transactionId) {
        Transaction tx = transactionRepo.findById(transactionId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy giao dịch"));

        if (tx.getStatus() != Transaction.TransactionStatus.WAITING_APPROVAL) {
            throw new RuntimeException("Giao dịch không hợp lệ để duyệt");
        }

        Card fromCard = tx.getFromCard();
        Card toCard = tx.getToCard();
        BigDecimal amount = tx.getAmount();

        // Giảm holdBalance người gửi
        Balance fromBalance = fromCard.getAccount().getBalance();
        System.out.println("Trước khi duyệt - FROM balance: available="
                + fromBalance.getAvailableBalance()
                + ", hold=" + fromBalance.getHoldBalance());

        fromBalance.setHoldBalance(fromBalance.getHoldBalance().subtract(amount));

        System.out.println("Sau khi trừ holdBalance - FROM balance: available="
                + fromBalance.getAvailableBalance()
                + ", hold=" + fromBalance.getHoldBalance());

        // Cộng vào availableBalance người nhận
        Balance toBalance = toCard.getAccount().getBalance();
        System.out.println("Trước khi duyệt - TO balance: available="
                + toBalance.getAvailableBalance()
                + ", hold=" + toBalance.getHoldBalance());

        toBalance.setAvailableBalance(toBalance.getAvailableBalance().add(amount));

        System.out.println("Sau khi cộng availableBalance - TO balance: available="
                + toBalance.getAvailableBalance()
                + ", hold=" + toBalance.getHoldBalance());

        // Lưu account
        accountRepository.save(fromCard.getAccount());
        accountRepository.save(toCard.getAccount());

        System.out.println(">>> Đã save cả 2 account vào DB");

        tx.setStatus(Transaction.TransactionStatus.SUCCESS);
        transactionRepo.save(tx);

        // Gửi message sang queue / service khác
        PaymentRequest payment = new PaymentRequest(
                tx.getTransactionId().toString(),
                fromCard.getAccount().getAccountId(),
                toCard.getAccount().getAccountId(),
                amount,
                "VND"
        );
        String url = "http://localhost:8081/api/payments";
        restTemplate.postForEntity(url, payment, String.class);

        return tx;
    }

    /**
     * Bước 3b: Admin từ chối giao dịch
     */
    @Transactional
    public Transaction rejectTransaction(Long transactionId) {
        Transaction tx = transactionRepo.findById(transactionId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy giao dịch"));

        if (tx.getStatus() != Transaction.TransactionStatus.WAITING_APPROVAL) {
            throw new RuntimeException("Giao dịch không hợp lệ để từ chối");
        }

        Card fromCard = tx.getFromCard();
        BigDecimal amount = tx.getAmount();

        // Giảm holdBalance người gửi
        fromCard.getAccount().getBalance().setHoldBalance(
                fromCard.getAccount().getBalance().getHoldBalance().subtract(amount)
        );

        // Hoàn lại vào availableBalance người gửi
        fromCard.getAccount().getBalance().setAvailableBalance(
                fromCard.getAccount().getBalance().getAvailableBalance().add(amount)
        );

        tx.setStatus(Transaction.TransactionStatus.FAILED);
        transactionRepo.save(tx);

        return tx;
    }
}
