package org.example.bankservice.service;

import org.example.bankservice.dto.*;
import org.example.bankservice.model.*;
import org.example.bankservice.repository.*;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AccountService {
    @Autowired private AccountRepository accountRepository;
    @Autowired private BalanceRepository balanceRepository;
    @Autowired private CardRepository cardRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private UserLevelRepository userLevelRepository;
    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired private EmailService emailService;

    // 🔹 CREATE
    @CachePut(value = "accounts_dto", key = "#result.accountId")
    public AccountResponseDTO create(AccountDTO accountDTO){
        if (userRepository.findByUsername(accountDTO.getUsername()).isPresent()) {
            throw new RuntimeException("Tên đăng nhập đã tồn tại");
        }
        User user = new User();
        user.setUsername(accountDTO.getUsername());
        user.setPassword(passwordEncoder.encode(accountDTO.getPassword()));
        user.setRole("USER");
        User savedUser = userRepository.save(user);

        if (accountRepository.findByEmail(accountDTO.getEmail()).isPresent()){
            throw new RuntimeException("Email đã tồn tại");
        }

        Account account = new Account();
        account.setCustomerName(accountDTO.getCustomerName());
        account.setEmail(accountDTO.getEmail());
        account.setPhoneNumber(accountDTO.getPhoneNumber());
        account.setUser(savedUser);

        Balance balance = new Balance();
        balance.setAvailableBalance(BigDecimal.ZERO);
        balance.setHoldBalance(BigDecimal.ZERO);
        balance.setAccount(account);
        account.setBalance(balance);

        String token = java.util.UUID.randomUUID().toString();
        account.setVerificationToken(token);
        account.setTokenExpiry(java.time.LocalDateTime.now().plusHours(24));
        account.setEmailVerified(false);

        Account saved = accountRepository.save(account);

        String link = "http://localhost:8080/api/auth/verify?token=" + token;
        emailService.sendEmail(
                accountDTO.getEmail(),
                "Xác thực tài khoản",
                "<p>Nhấn vào link để xác thực tài khoản:</p>"
                        + "<a href='" + link + "'>Xác thực ngay</a>"
        );
        return mapToDTO(saved);
    }

    // 🔹 UPDATE
    @Caching(
            put = { @CachePut(value = "accounts_dto", key = "#id") },
            evict = { @CacheEvict(value = "accounts_all_dto", allEntries = true) }
    )
    public AccountResponseDTO update(Long id, AccountDTO accountDTO){
        Account existing = accountRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy account với id: " + id));

        if (accountDTO.getCustomerName() != null && !accountDTO.getCustomerName().isEmpty()){
            existing.setCustomerName(accountDTO.getCustomerName());
        }
        if (accountDTO.getPhoneNumber() != null && !accountDTO.getPhoneNumber().isEmpty()){
            existing.setPhoneNumber(accountDTO.getPhoneNumber());
        }
        if (accountDTO.getUserLevelId() != null) {
            UserLevel level = userLevelRepository.findById(accountDTO.getUserLevelId())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy Level với id: " + accountDTO.getUserLevelId()));
            existing.setUserLevel(level);
        }

        Account saved = accountRepository.save(existing);
        return mapToDTO(saved);
    }

    // 🔹 DELETE
    @Caching(evict = {
            @CacheEvict(value = "accounts_dto", key = "#accountId"),
            @CacheEvict(value = "accounts_all_dto", allEntries = true)
    })
    public void delete(Long accountId){
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy tài khoản!"));

        List<Card> cards = cardRepository.findByAccount_AccountId(accountId);
        Balance balance = balanceRepository.findByAccount_AccountId(accountId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy số dư tài khoản!"));

        if (balance.getAvailableBalance().compareTo(BigDecimal.ZERO) > 0 ||
                balance.getHoldBalance().compareTo(BigDecimal.ZERO) > 0) {
            throw new RuntimeException("Không thể xóa tài khoản: số dư khác 0");
        }
        if (!cards.isEmpty()) {
            throw new RuntimeException("Không thể xóa tài khoản: còn liên kết thẻ");
        }

        accountRepository.delete(account);
    }

    // 🔹 GET BY ID
    @Cacheable(value = "accounts_dto", key = "#accountId")
    public AccountResponseDTO getAccountById(Long accountId) {
        Account acc = accountRepository.findByIdWithCards(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found"));
        return mapToDTO(acc);
    }

    // 🔹 GET ALL
    @Cacheable(value = "accounts_all_dto")
    public List<AccountResponseDTO> getAllAccount() {
        return accountRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    // 🔹 MAPPER
    public AccountResponseDTO mapToDTO(Account acc) {
        AccountResponseDTO dto = new AccountResponseDTO();
        dto.setAccountId(acc.getAccountId());
        dto.setCustomerName(acc.getCustomerName());
        dto.setEmail(acc.getEmail());
        dto.setPhoneNumber(acc.getPhoneNumber());

        if (acc.getBalance() != null) {
            BalanceDTO balanceDTO = new BalanceDTO();
            balanceDTO.setAccountId(acc.getAccountId());
            balanceDTO.setAvailableBalance(acc.getBalance().getAvailableBalance());
            balanceDTO.setHoldBalance(acc.getBalance().getHoldBalance());
            dto.setBalance(balanceDTO);
        }

        if (acc.getCards() != null) {
            List<CardDTO> cardDTOs = acc.getCards().stream().map(card -> {
                CardDTO c = new CardDTO();
                //c.setAccountId(acc.getAccountId());
                c.setCardId(card.getCardId());
                c.setCardNumber(card.getCardNumber());
                c.setCardType(card.getCardType());
                c.setExpiryDate(card.getExpiryDate());
                c.setStatus(card.getStatus());
                return c;
            }).collect(Collectors.toList());
            dto.setCards(cardDTOs);
        }

        if (acc.getUserLevel() != null) {
            UserLevelDTO lvl = new UserLevelDTO();
            lvl.setLevelName(acc.getUserLevel().getLevelName());
            lvl.setCardLimit(acc.getUserLevel().getCardLimit());
            lvl.setDailyTransferLimit(acc.getUserLevel().getDailyTransferLimit());
            dto.setUserLevel(lvl);
        }

        return dto;
    }
}

