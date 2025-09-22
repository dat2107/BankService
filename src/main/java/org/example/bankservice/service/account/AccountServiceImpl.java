package org.example.bankservice.service.account;

import lombok.AllArgsConstructor;
import org.example.bankservice.dto.*;
import org.example.bankservice.mapper.AccountMapper;
import org.example.bankservice.model.*;
import org.example.bankservice.repository.*;
import org.example.bankservice.service.EmailService;
import org.example.bankservice.validator.AccountValidator;
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
@AllArgsConstructor
public class AccountServiceImpl implements AccountService{
    private final AccountRepository accountRepository;
    private final BalanceRepository balanceRepository;
    private final CardRepository cardRepository;
    private final UserRepository userRepository;
    private final UserLevelRepository userLevelRepository;
    private final TransactionRepository transactionRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final AccountMapper accountMapper;
    private final AccountValidator accountValidator;

    // CREATE
    @Override
    @CachePut(value = "accounts_dto", key = "#result.accountId")
    public AccountResponseDTO create(AccountDTO accountDTO){
        accountValidator.validate(accountDTO);
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
        UserLevel normalLevel = userLevelRepository.findByLevelName("Normal")
                .orElseThrow(() -> new RuntimeException("Không tìm thấy Level Normal"));
        account.setUserLevel(normalLevel);
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
        return enrichWithHoldBalance(accountMapper.toDto(saved));
    }

    // UPDATE
    @Override
    @Caching(
            put = { @CachePut(value = "accounts_dto", key = "#id") },
            evict = { @CacheEvict(value = "accounts_all_dto", allEntries = true) }
    )
    public AccountResponseDTO update(Long id, AccountDTO accountDTO){
        Account existing = accountRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy account với id: " + id));

        if (accountDTO.getUserLevelId() != null) {
            UserLevel level = accountValidator.validateAndGetLevel(accountDTO.getUserLevelId());
            existing.setUserLevel(level);
        }

        Account saved = accountRepository.save(existing);
        return enrichWithHoldBalance(accountMapper.toDto(saved));
    }

    //DELETE
    @Override
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

        User user = account.getUser();

        accountRepository.delete(account);

        if (user != null) {
            userRepository.delete(user);
        }
    }

    //GET BY ID
    @Override
    @Cacheable(value = "accounts_dto", key = "#accountId")
    public AccountResponseDTO getAccountById(Long accountId) {
        Account acc = accountRepository.findByIdWithCards(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found"));
        return enrichWithHoldBalance(accountMapper.toDto(acc));
    }

    //GET ALL
    @Override
    @Cacheable(value = "accounts_all_dto")
    public List<AccountResponseDTO> getAllAccount() {
        return accountRepository.findAll().stream()
                .map(accountMapper::toDto)
                .map(this::enrichWithHoldBalance)
                .collect(Collectors.toList());
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "accounts_dto", key = "#accountId"),
            @CacheEvict(value = "accounts_all_dto", allEntries = true)
    })
    public void evictAccountCache(Long accountId) {

    }

    private AccountResponseDTO enrichWithHoldBalance(AccountResponseDTO dto) {
        if (dto.getCards() != null) {
            dto.getCards().forEach(c -> {
                BigDecimal holdBalance = transactionRepository
                        .findByFromCardAndStatus(
                                cardRepository.findById(c.getCardId())
                                        .orElseThrow(() -> new RuntimeException("Card not found")),
                                Transaction.TransactionStatus.WAITING_APPROVAL
                        )
                        .stream()
                        .map(Transaction::getAmount)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);

                c.setHoldBalance(holdBalance);
            });
        }
        return dto;
    }


    //MAPPER (maptruct)
    // tim hiẻu nguyên lý solid
}

