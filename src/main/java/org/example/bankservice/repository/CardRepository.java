package org.example.bankservice.repository;

import org.example.bankservice.model.Card;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CardRepository extends JpaRepository<Card, Long> {
    Boolean existsByAccount_AccountId(Long accountId);
    List<Card> findByAccount_AccountId(Long accountId);
    Optional<Card> findByCardNumber(String cardNumber);
    int countByAccount_AccountId(Long accountId);
}
