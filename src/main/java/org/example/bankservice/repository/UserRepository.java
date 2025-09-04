package org.example.bankservice.repository;

import org.example.bankservice.model.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    boolean existsByUsername(String username);
    @EntityGraph(attributePaths = {"account"})
    List<User> findAll();
    List<User> findByUsernameContainingIgnoreCase(String username);
}
