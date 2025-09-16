package org.example.bankservice.service;

import lombok.RequiredArgsConstructor;
import org.example.bankservice.dto.PaymentRequest;
import org.example.bankservice.dto.TransactionDTO;
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
    @Autowired
    private AccountService accountService;

    //Bước 1: Tạo giao dịch và sinh OTP
    @Transactional
    public Transaction createTransferRequest(TransferDTO dto) {
        Card fromCard = cardRepo.findById(dto.getFromCardId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy thẻ nguồn"));

        Card toCard = cardRepo.findByCardNumber(dto.getToCardNumber())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy thẻ nhận"));

        if (dto.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Số tiền không hợp lệ");
        }

        if (fromCard.getCardId().equals(toCard.getCardId())) {
            throw new RuntimeException("Không thể chuyển khoản sang cùng một thẻ");
        }

        if (fromCard.getAccount().getAccountId().equals(toCard.getAccount().getAccountId())) {
            throw new RuntimeException("Không thể chuyển khoản giữa các thẻ trong cùng một tài khoản");
        }

        if (fromCard.getAccount().getBalance().getAvailableBalance().compareTo(dto.getAmount()) < 0) {
            throw new RuntimeException("Số dư không đủ");
        }

        if (fromCard.getStatus() == Card.Status.INACTIVE) {
            throw new RuntimeException("Thẻ nguồn đã bị vô hiệu hóa, không thể chuyển khoản");
        }

        if (toCard.getStatus() == Card.Status.INACTIVE) {
            throw new RuntimeException("Thẻ nhận đã bị vô hiệu hóa, không thể chuyển khoản");
        }

        // Kiểm tra giới hạn giao dịch hằng ngày (Daily Transfer Limit)
        Account fromAccount = fromCard.getAccount();

        // Tính tổng số tiền đã chuyển thành công trong ngày hôm nay
        BigDecimal todayTotal = transactionRepo
                .findByFromCard_Account_AccountIdAndStatusAndCreatedAtBetween(
                        fromAccount.getAccountId(),
                        Transaction.TransactionStatus.SUCCESS,
                        LocalDateTime.now().toLocalDate().atStartOfDay(),
                        LocalDateTime.now().toLocalDate().atTime(23, 59, 59)
                )
                .stream()
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal dailyLimit = fromAccount.getUserLevel().getDailyTransferLimit();
        if (todayTotal.add(dto.getAmount()).compareTo(dailyLimit) > 0) {
            throw new RuntimeException("Vượt quá hạn mức chuyển khoản trong ngày (" + dailyLimit + ")");
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

    //Xác nhận OTP -> chuyển trạng thái sang WAITING_APPROVAL
    @Transactional
    public TransactionDTO confirmOtp(OtpConfirmDTO dto) {
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

        accountService.evictAccountCache(fromCard.getAccount().getAccountId());

        return toDto(tx);
    }

    //Admin duyệt giao dịch
    @Transactional
    public TransactionDTO approveTransaction(Long transactionId) {
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

        accountService.evictAccountCache(fromCard.getAccount().getAccountId());
        accountService.evictAccountCache(toCard.getAccount().getAccountId());

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

        payment.setSenderEmail(fromCard.getAccount().getEmail());

        String url = "http://payment-service:8080/api/payments";
        restTemplate.postForEntity(url, payment, String.class);

        return toDto(tx);
    }

    // Admin từ chối giao dịch
    @Transactional
    public TransactionDTO rejectTransaction(Long transactionId) {
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

        accountService.evictAccountCache(fromCard.getAccount().getAccountId());

        //Gửi email thông báo bị từ chối (best-effort, không phá giao dịch nếu gửi lỗi)
        try {
            String toEmail = fromCard.getAccount().getEmail();
            if (toEmail != null && !toEmail.isBlank()) {
                String subject = "Giao dịch chuyển tiền đã bị từ chối";
                String html = """
                <p>Xin chào <b>%s</b>,</p>
                <p>Giao dịch chuyển tiền của bạn đã <b>bị từ chối</b> bởi quản trị viên.</p>
                <ul>
                  <li><b>Mã giao dịch:</b> %s</li>
                  <li><b>Số thẻ nguồn:</b> %s</li>
                  <li><b>Số thẻ nhận:</b> %s</li>
                  <li><b>Số tiền:</b> %s</li>
                  <li><b>Thời điểm:</b> %s</li>
                  <li><b>Trạng thái:</b> %s</li>
                </ul>
                <p>Số tiền đã được hoàn lại vào số dư khả dụng của bạn.</p>
                <p>Nếu bạn không thực hiện yêu cầu này, vui lòng liên hệ hỗ trợ ngay.</p>
                """.formatted(
                        fromCard.getAccount().getCustomerName() != null ? fromCard.getAccount().getCustomerName() : "Quý khách",
                        tx.getTransactionId(),
                        fromCard.getCardNumber(),
                        tx.getToCard() != null ? tx.getToCard().getCardNumber() : "(không xác định)",
                        amount.toPlainString(),
                        LocalDateTime.now(),
                        tx.getStatus().name()
                );

                emailService.sendEmail(toEmail, subject, html);
            }
        } catch (Exception ignore) {

        }

        return toDto(tx);
    }

    public TransactionDTO toDto(Transaction tx) {
        TransactionDTO dto = new TransactionDTO();
        dto.setTransactionId(tx.getTransactionId());
        dto.setFromCardNumber(tx.getFromCard() != null ? tx.getFromCard().getCardNumber() : null);
        dto.setToCardNumber(tx.getToCard() != null ? tx.getToCard().getCardNumber() : null);
        dto.setAmount(tx.getAmount());
        dto.setStatus(tx.getStatus().name());
        dto.setCreatedAt(tx.getCreatedAt());
        return dto;
    }
}
