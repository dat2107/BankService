package org.example.bankservice.service.balance;

import org.example.bankservice.dto.BalanceDTO;
import org.example.bankservice.mapper.BalanceMapper;
import org.example.bankservice.model.Balance;
import org.example.bankservice.model.Card;
import org.example.bankservice.model.Transaction;
import org.example.bankservice.repository.AccountRepository;
import org.example.bankservice.repository.BalanceRepository;
import org.example.bankservice.repository.CardRepository;
import org.example.bankservice.repository.TransactionRepository;
import org.example.bankservice.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class BalanceServiceImpl implements BalanceService {
    @Autowired
    private BalanceRepository balanceRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private CardRepository cardRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private BalanceMapper balanceMapper;

    @Override
    public BalanceDTO getBalance(Long accountId) {
        Balance balance = balanceRepository.findByAccount_AccountId(accountId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy số dư cho accountId = " + accountId));
        return balanceMapper.toDto(balance);
    }

    @Override
    @Transactional
    public BalanceDTO deposit(Long accountId, BigDecimal amount, Long toCardId) {
        Balance balance = balanceRepository.findByAccount_AccountId(accountId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy số dư cho accountId = " + accountId));
        balance.setAvailableBalance(balance.getAvailableBalance().add(amount));
        Balance saved = balanceRepository.save(balance);

        Card toCard = cardRepository.findById(toCardId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy thẻ nhận"));

        if (toCard.getStatus() == Card.Status.INACTIVE) {
            throw new RuntimeException("Thẻ đã bị vô hiệu hóa, không thể giao dịch");
        }

        Transaction tx = Transaction.builder()
                .amount(amount)
                .toCard(toCard)
                .type(Transaction.TransactionType.DEPOSIT)
                .status(Transaction.TransactionStatus.SUCCESS)
                .createdAt(LocalDateTime.now())
                .build();
        transactionRepository.save(tx);

        // Gửi mail
        String email = toCard.getAccount().getEmail();
        if (email != null) {
            emailService.sendEmail(
                    email,
                    "Nạp tiền thành công",
                    "<p>Bạn đã nạp <b>" + amount + " VND</b> vào thẻ "
                            + toCard.getCardNumber() + ".</p>"
                            + "<p>Số dư hiện tại: " + balance.getAvailableBalance() + " VND</p>"
            );
        }

        return balanceMapper.toDto(saved);
    }

    @Override
    @Transactional
    public BalanceDTO withdraw(Long accountId, BigDecimal amount, Long fromCardId) {
        Balance balance = balanceRepository.findByAccount_AccountId(accountId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy số dư cho accountId = " + accountId));
        if (balance.getAvailableBalance().compareTo(amount) < 0) {
            throw new RuntimeException("Số dư không đủ để rút tiền");
        }
        balance.setAvailableBalance(balance.getAvailableBalance().subtract(amount));
        Balance saved = balanceRepository.save(balance);

        Card fromCard = cardRepository.findById(fromCardId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy thẻ nguồn"));

        if (fromCard.getStatus() == Card.Status.INACTIVE) {
            throw new RuntimeException("Thẻ đã bị vô hiệu hóa, không thể rút tiền");
        }
        Transaction tx = Transaction.builder()
                .amount(amount)
                .fromCard(fromCard)
                .type(Transaction.TransactionType.WITHDRAW)
                .status(Transaction.TransactionStatus.SUCCESS)
                .createdAt(LocalDateTime.now())
                .build();
        transactionRepository.save(tx);

        // Gửi mail
        String email = fromCard.getAccount().getEmail();
        if (email != null) {
            emailService.sendEmail(
                    email,
                    "Rút tiền thành công",
                    "<p>Bạn đã rút <b>" + amount + " VND</b> từ thẻ "
                            + fromCard.getCardNumber() + ".</p>"
                            + "<p>Số dư hiện tại: " + balance.getAvailableBalance() + " VND</p>"
            );
        }

        return balanceMapper.toDto(saved);
    }

}
