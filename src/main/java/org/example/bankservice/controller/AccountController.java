package org.example.bankservice.controller;

import org.example.bankservice.dto.AccountDTO;
import org.example.bankservice.dto.AccountResponseDTO;
import org.example.bankservice.model.Account;
import org.example.bankservice.repository.AccountRepository;
import org.example.bankservice.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/account")
public class AccountController {
    @Autowired
    private AccountService accountService;
    @Autowired
    private AccountRepository accountRepository;

    @PostMapping
    public ResponseEntity<?> create(@RequestBody AccountDTO accountDTO){
        AccountResponseDTO account = accountService.create(accountDTO);
        return ResponseEntity.ok("Thêm thành công! " + account);
    }

//    @GetMapping("/{accountId}")
//    public ResponseEntity<Account> getAccount(@PathVariable Long accountId){
//        Account account = accountService.getAccountById(accountId);
//        return ResponseEntity.ok(account);
//    }

    @GetMapping("/{accountId}")
    public ResponseEntity<AccountResponseDTO> getAccount(@PathVariable Long accountId) {
        AccountResponseDTO account = accountService.getAccountById(accountId);
        return ResponseEntity.ok(account);
    }


//    @GetMapping
//    public ResponseEntity<List<Account>> getAllAccount(){
//        List<Account> accounts = accountService.getAllAccount();
//        return ResponseEntity.ok(accounts);
//    }

    @GetMapping
    public ResponseEntity<List<AccountResponseDTO>> getAllAccount() {
        return ResponseEntity.ok(accountService.getAllAccount());
    }

    @PutMapping("/{accountId}")
    public ResponseEntity<?> update(@PathVariable Long accountId, @RequestBody AccountDTO accountDTO){
        AccountResponseDTO account = accountService.update(accountId,accountDTO);
        return ResponseEntity.ok("Sửa thành công! " + account);
    }

    @DeleteMapping("/{accountId}")
    public ResponseEntity<?> delete(@PathVariable Long accountId){
        accountService.delete(accountId);
        return ResponseEntity.ok("Xóa thành công! "+ accountId);
    }

    @GetMapping("/{id}/email")
    public ResponseEntity<String> getEmailByAccountId(@PathVariable Long id) {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy account"));
        return ResponseEntity.ok(account.getEmail());
    }
}
