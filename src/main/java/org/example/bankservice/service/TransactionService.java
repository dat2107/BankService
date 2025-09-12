package org.example.bankservice.service;

import lombok.RequiredArgsConstructor;
import org.example.bankservice.dto.TransactionDTO;
import org.example.bankservice.model.Transaction;
import org.example.bankservice.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TransactionService {
    @Autowired
    private TransactionRepository transactionRepository;

    public Page<TransactionDTO> getAll(String status, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Transaction> transactions;

        if (status != null) {
            transactions = transactionRepository.findByStatus(
                    Transaction.TransactionStatus.valueOf(status.toUpperCase()), pageable);
        } else {
            transactions = transactionRepository.findAll(pageable);
        }

        return transactions.map(this::toDto);
    }

    public List<TransactionDTO> findByAccountId(Long accountId) {
        List<Transaction> list = transactionRepository
                .findByFromCard_Account_AccountIdOrToCard_Account_AccountId(accountId, accountId);

        return list.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public Page<TransactionDTO> getByCard(Long cardId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Transaction> transactions =
                transactionRepository.findByFromCard_CardIdOrToCard_CardId(cardId, cardId, pageable);

        return transactions.map(this::toDto);
    }


    // Update status transaction
    public TransactionDTO updateStatus(Long id, String status) {
        Transaction tx = transactionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));
        tx.setStatus(Transaction.TransactionStatus.valueOf(status.toUpperCase()));
        Transaction saved = transactionRepository.save(tx);
        return toDto(saved);
    }

    // Convert Entity -> DTO
    public TransactionDTO toDto(Transaction tx) {
        TransactionDTO dto = new TransactionDTO();
        dto.setTransactionId(tx.getTransactionId());
        dto.setFromAccountId(tx.getFromCard() != null ? tx.getFromCard().getAccount().getAccountId() : null);
        dto.setToAccountId(tx.getToCard() != null ? tx.getToCard().getAccount().getAccountId() : null);
        dto.setFromCardNumber(tx.getFromCard() != null ? tx.getFromCard().getCardNumber() : null);
        dto.setToCardNumber(tx.getToCard() != null ? tx.getToCard().getCardNumber() : null);
        dto.setAmount(tx.getAmount());
        dto.setStatus(tx.getStatus().name());
        dto.setType(tx.getType().name());
        dto.setCreatedAt(tx.getCreatedAt());
        return dto;
    }
}
