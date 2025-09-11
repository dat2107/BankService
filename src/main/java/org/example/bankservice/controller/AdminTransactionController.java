package org.example.bankservice.controller;

import lombok.RequiredArgsConstructor;
import org.example.bankservice.dto.TransactionDTO;
import org.example.bankservice.model.Transaction;
import org.example.bankservice.service.TransferService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/transactions")
@RequiredArgsConstructor
public class AdminTransactionController {

    private final TransferService transferService;

    @PostMapping("/{id}/approve")
    public ResponseEntity<TransactionDTO> approve(@PathVariable Long id) {
        TransactionDTO tx = transferService.approveTransaction(id);
        return ResponseEntity.ok(tx);
    }

    @PostMapping("/{id}/reject")
    public ResponseEntity<TransactionDTO> reject(@PathVariable Long id) {
        TransactionDTO tx = transferService.rejectTransaction(id);
        return ResponseEntity.ok(tx);
    }
}
