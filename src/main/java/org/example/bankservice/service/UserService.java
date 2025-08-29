package org.example.bankservice.service;

import org.example.bankservice.dto.UserDTO;
import org.example.bankservice.model.User;
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

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;
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
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, password)
            );
            UserDetails userDetails = (UserDetails) auth.getPrincipal();
            return jwtUtil.generateToken(userDetails, user.getId());
        } catch (BadCredentialsException e) {
            System.out.println(">>> Sai mật khẩu hoặc tài khoản không tồn tại");
            throw new RuntimeException("Sai thông tin đăng nhập");
        }
    }

    public UserDTO checkLogin(String username, String password) {
        return userRepository.findByUsername(username)
                .filter(user -> passwordEncoder.matches(password, user.getPassword()))
                .map(user -> UserDTO.builder()
                        .username(user.getUsername())
                        .role(user.getRole())
                        .build())
                .orElse(null);
    }
}
