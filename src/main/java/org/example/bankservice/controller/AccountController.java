package org.example.bankservice.controller;

import org.example.bankservice.dto.AccountDTO;
import org.example.bankservice.model.Account;
import org.example.bankservice.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/account")
public class AccountController {
    @Autowired
    private AccountService accountService;

    @PostMapping
    public ResponseEntity<?> create(@RequestBody AccountDTO accountDTO){
        Account account = accountService.create(accountDTO);
        return ResponseEntity.ok("Thêm thành công! " + account);
    }

    @GetMapping("/{accountId}")
    public ResponseEntity<Account> getAccount(@PathVariable Long accountId){
        Account account = accountService.getAccountById(accountId);
        return ResponseEntity.ok(account);
    }

    @PutMapping("/{accountId}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody AccountDTO accountDTO){
        Account account = accountService.update(id,accountDTO);
        return ResponseEntity.ok("Sửa thành công! " + account);
    }

    @DeleteMapping("/{accountId}")
    public ResponseEntity<?> delete(@PathVariable Long accountId){
        accountService.delete(accountId);
        return ResponseEntity.ok("Xóa thành công! "+ accountId);
    }
}
