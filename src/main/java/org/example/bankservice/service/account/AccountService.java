package org.example.bankservice.service.account;

import org.example.bankservice.dto.AccountDTO;
import org.example.bankservice.dto.AccountResponseDTO;

import java.util.List;

public interface AccountService {
    AccountResponseDTO create(AccountDTO accountDTO);
    AccountResponseDTO update(Long id, AccountDTO accountDTO);
    void delete(Long accountId);
    AccountResponseDTO getAccountById(Long accountId);
    List<AccountResponseDTO> getAllAccount();
    void evictAccountCache(Long accountId);
}
