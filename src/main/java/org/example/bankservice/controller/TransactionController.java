package org.example.bankservice.controller;

import lombok.RequiredArgsConstructor;
import org.example.bankservice.dto.TransactionDTO;
import org.example.bankservice.model.Transaction;
import org.example.bankservice.repository.TransactionRepository;
import org.example.bankservice.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transaction")
@RequiredArgsConstructor
public class TransactionController {
    @Autowired
    private TransactionService transactionService;

    @Autowired
    private TransactionRepository transactionRepo;

    //Lấy tất cả giao dịch (Admin)
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<Page<TransactionDTO>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "7") int size,
            @RequestParam(required = false) String status) {
        return ResponseEntity.ok(transactionService.getAll(status, page, size));
    }

    //Lấy giao dịch theo accountId (User)
    @GetMapping("/account/{accountId}")
    public ResponseEntity<List<TransactionDTO>> getTransactionsByAccount(@PathVariable Long accountId) {
        List<TransactionDTO> transactions = transactionService.findByAccountId(accountId);
        return ResponseEntity.ok(transactions);
    }

    //Lấy giao dịch theo cardId (User)
    @GetMapping("/card/{cardId}")
    public ResponseEntity<Page<TransactionDTO>> getByCard(
            @PathVariable Long cardId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "7") int size) {
        return ResponseEntity.ok(transactionService.getByCard(cardId, page, size));
    }


    //Cập nhật trạng thái giao dịch
    @PutMapping("/{id}/status")
    public ResponseEntity<TransactionDTO> updateStatus(
            @PathVariable Long id,
            @RequestBody TransactionDTO request) {
        return ResponseEntity.ok(transactionService.updateStatus(id, request.getStatus()));
    }

    //Xem chi tiết 1 giao dịch
    @GetMapping("/{id}")
    public ResponseEntity<TransactionDTO> getOne(@PathVariable Long id) {
        return transactionRepo.findById(id)
                .map(tx -> ResponseEntity.ok(transactionService.toDto(tx)))
                .orElse(ResponseEntity.notFound().build());
    }

}
