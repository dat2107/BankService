package org.example.bankservice.service;

import org.example.bankservice.dto.AccountDTO;
import org.example.bankservice.model.*;
import org.example.bankservice.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class AccountService {
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private BalanceRepository balanceRepository;
    @Autowired
    private CardRepository cardRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserLevelRepository userLevelRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @CachePut(value = "accounts", key = "#result.accountId")
    public Account create(AccountDTO accountDTO){
        if (userRepository.findByUsername(accountDTO.getUsername()).isPresent()) {
            throw new RuntimeException("Tên đăng nhập đã tồn tại");
        }
        User user = new User();// hoặc tự sinh theo logic khác
        user.setUsername(accountDTO.getUsername());
        user.setPassword(passwordEncoder.encode(accountDTO.getPassword()));
        user.setRole("USER"); // Mặc định là CUSTOMER
        userRepository.save(user);
        if (accountRepository.findByEmail(accountDTO.getEmail()).isPresent()){
            throw new RuntimeException("email đã tồn tại");
        }
        Account account = new Account();
        account.setCustomerName(accountDTO.getCustomerName());
        account.setEmail(accountDTO.getEmail());
        account.setPhoneNumber(accountDTO.getPhoneNumber());
        account.setUser(user);

        Balance balance = new Balance();
        balance.setAvailableBalance(BigDecimal.ZERO);
        balance.setHoldBalance(BigDecimal.ZERO);
        balance.setAccount(account);

        account.setBalance(balance);

        return accountRepository.save(account);
    }

    @Caching(
            put = { @CachePut(value = "accounts", key = "#id") },
            evict = { @CacheEvict(value = "accounts_all", allEntries = true) }
    )
    public Account update(Long id,AccountDTO accountDTO){
        return accountRepository.findById(id)
                .map(existing -> {
                    if (accountDTO.getCustomerName() != null && !accountDTO.getCustomerName().isEmpty()){
                        existing.setCustomerName(accountDTO.getCustomerName());
                    }
                    if (accountDTO.getPhoneNumber() != null && !accountDTO.getPhoneNumber().isEmpty()){
                        existing.setPhoneNumber(accountDTO.getPhoneNumber());
                    }
                    if (accountDTO.getUserLevelId() != null) {   // ✅ lấy id
                        UserLevel level = userLevelRepository.findById(accountDTO.getUserLevelId())
                                .orElseThrow(() -> new RuntimeException("Không tìm thấy Level với id: " + accountDTO.getUserLevelId()));
                        existing.setUserLevel(level);   // ✅ gán entity
                    }

                    return accountRepository.save(existing);
                })
                .orElseThrow(() -> new RuntimeException("Không tìm người đề xuất với id: " + id));
    }

    @Caching(evict = {
            @CacheEvict(value = "accounts", key = "#accountId"),
            @CacheEvict(value = "accounts_all", allEntries = true)
    })
    public void delete(Long accountId){
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy tài khoản!"));
        List<Card> cards = cardRepository.findByAccount_AccountId(accountId);
        for(Card s: cards){
            Balance balance = balanceRepository.findByAccount_AccountId(accountId)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy số dư tài khoản!"));
            if (balance.getAvailableBalance().compareTo(BigDecimal.ZERO) > 0 ||
                    balance.getHoldBalance().compareTo(BigDecimal.ZERO) > 0) {
                throw new RuntimeException("Không thể xóa tài khoản: Số dư tài khoản khác 0");
            }
        }
        if (!cards.isEmpty()) {
            throw new RuntimeException("Cannot delete account: linked cards exist (even with zero balance)");
        }
        accountRepository.delete(account);
    }

    @Cacheable(value = "accounts", key = "#accountId")
    public Account findById(Long accountId){
        return accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy account với id: "+accountId));
    }

    @Cacheable(value = "accounts", key = "#id")
    public Account getAccountById(Long id) {
        return accountRepository.findByAccountId(id)
                .orElseThrow(() -> new RuntimeException("Account not found"));
    }

    @Cacheable(value = "accounts_all")
    public List<Account> getAllAccount(){
        return accountRepository.findAll();
    }
}
