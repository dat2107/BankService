package org.example.bankservice.controller;

import org.example.bankservice.model.Balance;
import org.example.bankservice.service.BalanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/balance")
public class BalanceController {
    @Autowired
    private BalanceService balanceService;

    @GetMapping("/{accountId}")
    public ResponseEntity<?> getBalance(@PathVariable Long accountId) {
        Balance balance = balanceService.getBalance(accountId);
        return ResponseEntity.ok(balance);
    }

    @PostMapping("/{accountId}/deposit")
    public ResponseEntity<?> deposit(@PathVariable Long accountId, @RequestParam BigDecimal amount) {
        Balance balance = balanceService.deposit(accountId, amount);
        return ResponseEntity.ok("Nạp tiền thành công. Số dư mới: " + balance.getAvailableBalance());
    }

    @PostMapping("/{accountId}/withdraw")
    public ResponseEntity<?> withdraw(@PathVariable Long accountId, @RequestParam BigDecimal amount) {
        Balance balance = balanceService.withdraw(accountId, amount);
        return ResponseEntity.ok("Rút tiền thành công. Số dư mới: " + balance.getAvailableBalance());
    }
}
