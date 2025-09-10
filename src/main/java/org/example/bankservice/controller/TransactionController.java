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

    /**
     * Lấy tất cả giao dịch (Admin)
     * Có thể filter theo status: /api/transaction?status=SUCCESS
     */
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<Page<TransactionDTO>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "7") int size,
            @RequestParam(required = false) String status) {
        return ResponseEntity.ok(transactionService.getAll(status, page, size));
    }

    /**
     * Lấy giao dịch theo accountId (User)
     */
    @GetMapping("/account/{accountId}")
    public ResponseEntity<Page<TransactionDTO>> getByAccount(
            @PathVariable Long accountId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "7") int size) {
        return ResponseEntity.ok(transactionService.getByAccount(accountId, page, size));
    }

    /**
     * Lấy giao dịch theo cardId (User)
     */
    @GetMapping("/card/{cardId}")
    public ResponseEntity<Page<TransactionDTO>> getByCard(
            @PathVariable Long cardId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "7") int size) {
        return ResponseEntity.ok(transactionService.getByCard(cardId, page, size));
    }


    /**
     * Cập nhật trạng thái giao dịch
     */
    @PutMapping("/{id}/status")
    public ResponseEntity<TransactionDTO> updateStatus(
            @PathVariable Long id,
            @RequestBody TransactionDTO request) {
        return ResponseEntity.ok(transactionService.updateStatus(id, request.getStatus()));
    }

    /**
     * Lấy toàn bộ giao dịch (admin xem)
     */
//    @GetMapping
//    public ResponseEntity<List<Transaction>> getAll() {
//        return ResponseEntity.ok(transactionRepo.findAll());
//    }

    /**
     * Xem chi tiết 1 giao dịch
     */
    @GetMapping("/{id}")
    public ResponseEntity<Transaction> getOne(@PathVariable Long id) {
        return transactionRepo.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
