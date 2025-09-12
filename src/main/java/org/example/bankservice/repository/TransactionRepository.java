package org.example.bankservice.repository;

import org.example.bankservice.model.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TransactionRepository  extends JpaRepository<Transaction, Long> {
    List<Transaction> findByStatus(Transaction.TransactionStatus status);

    // Có phân trang
    Page<Transaction> findByStatus(Transaction.TransactionStatus status, Pageable pageable);

    Page<Transaction> findByFromCard_Account_AccountIdOrToCard_Account_AccountId(
            Long fromAccountId, Long toAccountId, Pageable pageable);

    Page<Transaction> findByFromCard_CardIdOrToCard_CardId(
            Long fromCardId, Long toCardId, Pageable pageable);

    @Query("""
       SELECT t FROM Transaction t
       WHERE (t.fromCard IS NOT NULL AND t.fromCard.account.accountId = :accountId)
          OR (t.toCard IS NOT NULL AND t.toCard.account.accountId = :accountId)
       ORDER BY t.createdAt DESC
       """)
    List<Transaction> findByAccount_AccountId(@Param("accountId") Long accountId);

}
