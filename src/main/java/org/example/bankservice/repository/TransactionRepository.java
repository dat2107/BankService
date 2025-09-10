package org.example.bankservice.repository;

import org.example.bankservice.model.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransactionRepository  extends JpaRepository<Transaction, Long> {
    List<Transaction> findByStatus(Transaction.TransactionStatus status);

    // Có phân trang
    Page<Transaction> findByStatus(Transaction.TransactionStatus status, Pageable pageable);

    Page<Transaction> findByFromCard_Account_AccountIdOrToCard_Account_AccountId(
            Long fromAccountId, Long toAccountId, Pageable pageable);

    Page<Transaction> findByFromCard_CardIdOrToCard_CardId(
            Long fromCardId, Long toCardId, Pageable pageable);
}
