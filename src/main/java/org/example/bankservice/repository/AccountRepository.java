package org.example.bankservice.repository;

import org.example.bankservice.model.Account;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {
    Optional<Account> findByEmail(String email);
    Boolean existsByAccountId(Long accountId);
    Optional<Account> findByUserId(Long userId);
    @EntityGraph(attributePaths = {"cards"})
    Optional<Account> findByAccountId(Long accountId);
    List<Account> findAll();
    @Query("SELECT a FROM Account a LEFT JOIN FETCH a.cards WHERE a.accountId = :id")
    Optional<Account> findByIdWithCards(@Param("id") Long id);
    Optional<Account> findByVerificationToken(String token);
}
