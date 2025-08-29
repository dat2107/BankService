package org.example.bankservice.service;

import org.example.bankservice.model.Balance;
import org.example.bankservice.repository.AccountRepository;
import org.example.bankservice.repository.BalanceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class BalanceService {
    @Autowired
    private BalanceRepository balanceRepository;

    @Autowired
    private AccountRepository accountRepository;

    public Balance getBalance(Long accountId) {
        return balanceRepository.findByAccount_AccountId(accountId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy số dư cho accountId = " + accountId));
    }

    public Balance deposit(Long accountId, BigDecimal amount) {
        Balance balance = getBalance(accountId);
        balance.setAvailableBalance(balance.getAvailableBalance().add(amount));
        return balanceRepository.save(balance);
    }

    public Balance withdraw(Long accountId, BigDecimal amount) {
        Balance balance = getBalance(accountId);
        if (balance.getAvailableBalance().compareTo(amount) < 0) {
            throw new RuntimeException("Số dư không đủ để rút tiền");
        }
        balance.setAvailableBalance(balance.getAvailableBalance().subtract(amount));
        return balanceRepository.save(balance);
    }


}
