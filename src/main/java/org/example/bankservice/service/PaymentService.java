package org.example.bankservice.service;

import org.example.bankservice.dto.PaymentRequest;
import org.example.bankservice.model.Account;
import org.example.bankservice.model.Balance;
import org.example.bankservice.repository.AccountRepository;
import org.example.bankservice.repository.BalanceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class PaymentService {
    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private BalanceRepository balanceRepository;

    @Autowired
    private PaymentProducer paymentProducer;

    public void processPayment(PaymentRequest request) {
        // Tạo paymentId nếu chưa có
        if (request.getPaymentId() == null || request.getPaymentId().isEmpty()) {
            request.setPaymentId(UUID.randomUUID().toString());
        }

        // 1. Kiểm tra account tồn tại
        Account account = accountRepository.findById(request.getFromAccountId())
                .orElseThrow(() -> new RuntimeException("Account not found!"));

        // 2. Lấy balance
        Balance balance = balanceRepository.findByAccount_AccountId(account.getAccountId())
                .orElseThrow(() -> new RuntimeException("Balance not found!"));

        // 3. Kiểm tra số dư
        if (balance.getAvailableBalance().compareTo(request.getAmount()) < 0) {
            throw new RuntimeException("Insufficient balance!");
        }

        // 4. Trừ tiền
        balance.setAvailableBalance(balance.getAvailableBalance().subtract(request.getAmount()));
        balanceRepository.save(balance);

        // 5. Gửi message sang queue
        paymentProducer.sendPayment(request);
    }
}
