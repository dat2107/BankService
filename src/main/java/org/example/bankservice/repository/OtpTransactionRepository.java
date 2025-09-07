package org.example.bankservice.repository;

import org.example.bankservice.model.OtpTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OtpTransactionRepository extends JpaRepository<OtpTransaction, Long> {
    OtpTransaction findByTransaction_TransactionId(Long transactionId);
}
