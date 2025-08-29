package org.example.bankservice.controller;

import jakarta.servlet.http.HttpServletResponse;
import org.example.bankservice.dto.AccountDTO;
import org.example.bankservice.dto.UserDTO;
import org.example.bankservice.model.Account;
import org.example.bankservice.service.AccountService;
import org.example.bankservice.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    private UserService userService;
    @Autowired
    private AccountService accountService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody AccountDTO accountDTO){
        Account account = accountService.create(accountDTO);
        return ResponseEntity.ok("Đăng ký thành công cho user: " + account.getCustomerName());
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UserDTO request) {
        String token = userService.login(request.getUsername(), request.getPassword());
        return ResponseEntity.ok().body(token);
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(Authentication authentication) {
        // Authentication được set trong JwtFilter
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).body("Chưa đăng nhập");
        }

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String username = userDetails.getUsername();
        String role = userDetails.getAuthorities().iterator().next().getAuthority();

        Map<String, String> response = new HashMap<>();
        response.put("username", username);
        response.put("role", role);

        return ResponseEntity.ok(response);
    }


}
