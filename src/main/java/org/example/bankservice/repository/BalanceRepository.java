package org.example.bankservice.repository;

import org.example.bankservice.model.Balance;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BalanceRepository extends JpaRepository<Balance,Long> {
    Optional<Balance> findById(Long balanceId);
    Optional<Balance> findByAccount_AccountId(Long accountId);
}
