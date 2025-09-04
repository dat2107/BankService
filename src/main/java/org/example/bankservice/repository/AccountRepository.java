package org.example.bankservice.repository;

import org.example.bankservice.model.Account;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {
    Optional<Account> findByEmail(String email);
    Boolean existsByAccountId(Long accountId);
    Optional<Account> findByUserId(Long userId);
    @EntityGraph(attributePaths = {"cards"})
    Optional<Account> findByAccountId(Long accountId);
}
