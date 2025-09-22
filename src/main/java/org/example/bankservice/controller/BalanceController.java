package org.example.bankservice.controller;

import org.example.bankservice.dto.BalanceDTO;
import org.example.bankservice.model.Balance;

import org.example.bankservice.service.balance.BalanceService;
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
    public ResponseEntity<BalanceDTO> getBalance(@PathVariable Long accountId) {
        return ResponseEntity.ok(balanceService.getBalance(accountId));
    }

    @PostMapping("/{accountId}/deposit")
    public ResponseEntity<BalanceDTO> deposit(@PathVariable Long accountId, @RequestParam BigDecimal amount, @RequestParam Long toCardId) {
        return ResponseEntity.ok( balanceService.deposit(accountId, amount, toCardId));
    }

    @PostMapping("/{accountId}/withdraw")
    public ResponseEntity<BalanceDTO> withdraw(@PathVariable Long accountId, @RequestParam BigDecimal amount, @RequestParam Long fromCardId) {

        return ResponseEntity.ok(balanceService.withdraw(accountId, amount, fromCardId));
    }
}
