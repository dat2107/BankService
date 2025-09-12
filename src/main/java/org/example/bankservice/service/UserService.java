package org.example.bankservice.service;

import org.example.bankservice.dto.UserDTO;
import org.example.bankservice.model.Account;
import org.example.bankservice.model.User;
import org.example.bankservice.repository.AccountRepository;
import org.example.bankservice.repository.UserRepository;
import org.example.bankservice.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private JwtUtil jwtUtil;

    public String login(String username, String password) {
        try {
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy user"));
            // Xác thực username/password
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, password)
            );
            UserDetails userDetails = (UserDetails) auth.getPrincipal();
            // Lấy account gắn với user (1 user = 1 account)
            Account account = accountRepository.findByUserId(user.getId())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy account cho user"));
            // Sinh token chứa accountId + role
            return jwtUtil.generateToken(userDetails, account);
        } catch (BadCredentialsException e) {
            System.out.println(">>> Sai mật khẩu hoặc tài khoản không tồn tại");
            throw new RuntimeException("Sai thông tin đăng nhập");
        }
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public List<User> searchUsers(String keyword) {
        return userRepository.findByUsernameContainingIgnoreCase(keyword);
    }

}
