package org.example.bankservice.service.balance;

import org.example.bankservice.dto.BalanceDTO;
import org.example.bankservice.model.Balance;

import java.math.BigDecimal;

public interface BalanceService {
    BalanceDTO getBalance(Long accountId);
    BalanceDTO deposit(Long accountId, BigDecimal amount, Long toCardId);
    BalanceDTO withdraw(Long accountId, BigDecimal amount, Long fromCardId);
}
