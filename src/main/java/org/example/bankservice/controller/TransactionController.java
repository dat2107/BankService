package org.example.bankservice.controller;

import lombok.RequiredArgsConstructor;
import org.example.bankservice.dto.TransactionDTO;
import org.example.bankservice.model.Transaction;
import org.example.bankservice.repository.TransactionRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionRepository transactionRepo;

    /**
     * Lấy tất cả giao dịch (Admin)
     * Có thể filter theo status: /api/transactions?status=SUCCESS
     */
    @GetMapping
    public ResponseEntity<List<TransactionDTO>> getAll(
            @RequestParam(required = false) String status) {
        List<Transaction> transactions = transactionRepo.findAll();

        if (status != null) {
            transactions = transactions.stream()
                    .filter(tx -> tx.getStatus().name().equalsIgnoreCase(status))
                    .toList();
        }

        return ResponseEntity.ok(transactions.stream()
                .map(this::toDto)
                .collect(Collectors.toList()));
    }

    /**
     * Lấy giao dịch theo accountId (User)
     */
    @GetMapping("/account/{accountId}")
    public ResponseEntity<List<TransactionDTO>> getByAccount(@PathVariable Long accountId) {
        List<Transaction> transactions = transactionRepo.findAll().stream()
                .filter(tx ->
                        tx.getFromCard().getAccount().getAccountId().equals(accountId) ||
                                tx.getToCard().getAccount().getAccountId().equals(accountId)
                )
                .toList();

        return ResponseEntity.ok(transactions.stream()
                .map(this::toDto)
                .collect(Collectors.toList()));
    }

    /**
     * Lấy giao dịch theo cardId (User)
     */
    @GetMapping("/card/{cardId}")
    public ResponseEntity<List<TransactionDTO>> getByCard(@PathVariable Long cardId) {
        List<Transaction> transactions = transactionRepo.findAll().stream()
                .filter(tx ->
                        tx.getFromCard().getCardId().equals(cardId) ||
                                tx.getToCard().getCardId().equals(cardId)
                )
                .toList();

        return ResponseEntity.ok(transactions.stream()
                .map(this::toDto)
                .collect(Collectors.toList()));
    }

    // Convert Entity -> DTO
    private TransactionDTO toDto(Transaction tx) {
        TransactionDTO dto = new TransactionDTO();
        dto.setTransactionId(tx.getTransactionId());
        dto.setFromCardNumber(tx.getFromCard().getCardNumber());
        dto.setToCardNumber(tx.getToCard().getCardNumber());
        dto.setAmount(tx.getAmount());
        dto.setStatus(tx.getStatus().name());
        dto.setCreatedAt(tx.getCreatedAt());
        return dto;
    }
}
