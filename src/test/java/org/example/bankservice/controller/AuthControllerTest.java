package org.example.bankservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.bankservice.config.SecurityConfig;
import org.example.bankservice.dto.AccountDTO;
import org.example.bankservice.dto.AccountResponseDTO;
import org.example.bankservice.model.Account;
import org.example.bankservice.repository.AccountRepository;
import org.example.bankservice.security.JwtUtil;
import org.example.bankservice.service.account.AccountServiceImpl;
import org.example.bankservice.service.UserService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Optional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(AuthController.class)
@Import(SecurityConfig.class)// 👈 chỉ load AuthController + MockMvc
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    // Mock service
    @MockitoBean
    private UserService authService;

    @MockitoBean
    private AccountRepository accountRepository;

    @MockitoBean
    private JwtUtil jwtUtil;

    @MockitoBean
    private AccountServiceImpl accountService;

    @MockitoBean
    private UserDetailsService userDetailsService;

    @Test
    void login_success() throws Exception {
        HashMap<String, String> payload = new HashMap<>();
        payload.put("username", "Quỳnh");
        payload.put("password", "12345678");

        // giả lập login trả về token
        Mockito.when(authService.login(Mockito.anyString(), Mockito.anyString()))
                .thenReturn("jwt-token");

        mockMvc.perform(post("/api/auth/login")
                       // ✅ thêm CSRF token giả lập
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payload)))
                .andDo(result -> {
                    System.out.println(">>> Status: " + result.getResponse().getStatus());
                    System.out.println(">>> Body: " + result.getResponse().getContentAsString());
                })
                .andExpect(status().isOk())
                .andExpect(content().string("jwt-token"));

    }

    @Test
    void login_fail_wrongPassword() throws Exception {
        var payload = new HashMap<String, String>();
        payload.put("username", "Quỳnh");
        payload.put("password", "bad");

        Mockito.when(authService.login(Mockito.anyString(), Mockito.anyString()))
                .thenThrow(new RuntimeException("Sai mật khẩu"));

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payload)))
                .andDo(result -> {
                    System.out.println(">>> Status: " + result.getResponse().getStatus());
                    System.out.println(">>> Body: " + result.getResponse().getContentAsString());
                })
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Sai mật khẩu"));
    }

    @Test
    void register_success() throws Exception {
        var payload = new java.util.HashMap<String, String>();
        payload.put("username", "new");
        payload.put("email", "a@b.com");
        payload.put("password", "123456");
        payload.put("phoneNumber", "0123456789");
        payload.put("customerName", "Alice");

        var mockAccount = new AccountResponseDTO();
        mockAccount.setCustomerName("Alice");
        // Mock service trả về DTO
        Mockito.when(accountService.create(Mockito.any(AccountDTO.class)))
                .thenReturn(mockAccount);

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payload)))
                .andDo(result -> {
                    System.out.println(">>> Status: " + result.getResponse().getStatus());
                    System.out.println(">>> Body: " + result.getResponse().getContentAsString());
                })
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Alice")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("thành công")));
    }

    @Test
    void register_fail_duplicate() throws Exception {
        Mockito.when(accountService.create(Mockito.any()))
                .thenThrow(new RuntimeException("Username đã tồn tại"));

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new java.util.HashMap<String, String>())))
                .andDo(result -> {
                    System.out.println(">>> Status: " + result.getResponse().getStatus());
                    System.out.println(">>> Body: " + result.getResponse().getContentAsString());
                })
                .andExpect(status().isBadRequest()) // ✅ đổi từ 5xx -> 400
                .andExpect(content().string("Username đã tồn tại"));
    }

    @Test
    void verifyEmail_success() throws Exception {
        // Giả lập có account hợp lệ trong DB
        Account mockAcc = new Account();
        mockAcc.setEmailVerified(false);
        mockAcc.setVerificationToken("abc");
        mockAcc.setTokenExpiry(LocalDateTime.now().plusHours(1));

        Mockito.when(accountRepository.findByVerificationToken("abc"))
                .thenReturn(Optional.of(mockAcc));

        mockMvc.perform(get("/api/auth/verify").param("token", "abc"))
                .andDo(result -> {
                    System.out.println(">>> Status: " + result.getResponse().getStatus());
                    System.out.println(">>> Body: " + result.getResponse().getContentAsString());
                })
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Xác thực email thành công")));
    }

    @Test
    void verifyEmail_fail() throws Exception {
        // Giả lập không tìm thấy token
        Mockito.when(accountRepository.findByVerificationToken("bad"))
                .thenReturn(Optional.empty());

        mockMvc.perform(get("/api/auth/verify").param("token", "bad"))
                .andDo(result -> {
                    System.out.println(">>> Status: " + result.getResponse().getStatus());
                    System.out.println(">>> Body: " + result.getResponse().getContentAsString());
                })
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Token không hợp lệ"));
    }


}

