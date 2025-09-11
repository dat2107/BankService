package org.example.bankservice.controller;

import org.example.bankservice.dto.BalanceDTO;
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
    public ResponseEntity<BalanceDTO> getBalance(@PathVariable Long accountId) {
        Balance balance = balanceService.getBalance(accountId);
        return ResponseEntity.ok(balanceService.mapToDTO(balance));
    }

    @PostMapping("/{accountId}/deposit")
    public ResponseEntity<BalanceDTO> deposit(@PathVariable Long accountId, @RequestParam BigDecimal amount, @RequestParam Long toCardId) {
        Balance balance = balanceService.deposit(accountId, amount, toCardId);
        return ResponseEntity.ok(balanceService.mapToDTO(balance));
    }

    @PostMapping("/{accountId}/withdraw")
    public ResponseEntity<BalanceDTO> withdraw(@PathVariable Long accountId, @RequestParam BigDecimal amount, @RequestParam Long fromCardId) {
        Balance balance = balanceService.withdraw(accountId, amount, fromCardId);
        return ResponseEntity.ok(balanceService.mapToDTO(balance));
    }
}
