package org.example.bankservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.bankservice.config.SecurityConfig;
import org.example.bankservice.dto.AccountDTO;
import org.example.bankservice.dto.AccountResponseDTO;
import org.example.bankservice.model.Account;
import org.example.bankservice.repository.AccountRepository;
import org.example.bankservice.security.JwtUtil;
import org.example.bankservice.service.AccountService;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AccountController.class)
@Import(SecurityConfig.class)
class AccountControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private JwtUtil jwtUtil;
    @MockitoBean
    private AccountService accountService;
    @MockitoBean
    private AccountRepository accountRepository;
    @MockitoBean
    private UserDetailsService userDetailsService;

    @Test
    void getAccount_success() throws Exception {
        AccountResponseDTO resp = new AccountResponseDTO();
        resp.setAccountId(1L);
        resp.setCustomerName("demo");

        Mockito.when(accountService.getAccountById(1L)).thenReturn(resp);

        mockMvc.perform(get("/api/account/1"))
                .andDo(result -> {
                    System.out.println(">>> Status: " + result.getResponse().getStatus());
                    System.out.println(">>> Body: " + result.getResponse().getContentAsString());
                })
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customerName").value("demo"));

    }

    @Test
    void getAccount_fail() throws Exception {
        Mockito.when(accountService.getAccountById(99L))
                .thenThrow(new RuntimeException("Không tìm thấy"));

        mockMvc.perform(get("/api/account/99"))
                .andDo(result -> {
                    System.out.println(">>> Status: " + result.getResponse().getStatus());
                    System.out.println(">>> Body: " + result.getResponse().getContentAsString());
                })
                .andExpect(status().isBadRequest()) // ✅ đổi từ 5xx -> 400
                .andExpect(content().string("Không tìm thấy"));
    }

    @Test
    void getAllAccounts_success() throws Exception {
        AccountResponseDTO dto1 = new AccountResponseDTO();
        dto1.setAccountId(1L);
        dto1.setCustomerName("u1");

        AccountResponseDTO dto2 = new AccountResponseDTO();
        dto2.setAccountId(2L);
        dto2.setCustomerName("u2");

        Mockito.when(accountService.getAllAccount()).thenReturn(List.of(dto1, dto2));


        mockMvc.perform(get("/api/account"))
                .andExpect(status().isOk())
                .andDo(result -> {
                    System.out.println(">>> Status: " + result.getResponse().getStatus());
                    System.out.println(">>> Body: " + result.getResponse().getContentAsString());
                })
                .andExpect(jsonPath("$[0].customerName").value("u1"))
                .andExpect(jsonPath("$[1].customerName").value("u2"));
    }

    @Test
    void getAllAccounts_fail() throws Exception {
        Mockito.when(accountService.getAllAccount())
                .thenThrow(new RuntimeException("DB error"));

        mockMvc.perform(get("/api/account"))
                .andDo(result -> {
                    System.out.println(">>> Status: " + result.getResponse().getStatus());
                    System.out.println(">>> Body: " + result.getResponse().getContentAsString());
                })
                .andExpect(status().isBadRequest())
                .andExpect(content().string("DB error"));

    }

    @Test
    void updateAccount_success() throws Exception {
        AccountDTO dto = new AccountDTO();
        dto.setUsername("updated");

        AccountResponseDTO resp = new AccountResponseDTO();
        resp.setAccountId(1L);
        resp.setCustomerName("updated");

        Mockito.when(accountService.update(Mockito.eq(1L), Mockito.any())).thenReturn(resp);

        mockMvc.perform(put("/api/account/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andDo(result -> {
                    System.out.println(">>> Status: " + result.getResponse().getStatus());
                    System.out.println(">>> Body: " + result.getResponse().getContentAsString());
                })
                .andExpect(status().isOk())
                .andExpect(content().string(Matchers.containsString("Sửa thành công!")))
                .andExpect(content().string(Matchers.containsString("updated")));
    }

    @Test
    void updateAccount_fail() throws Exception {
        Mockito.when(accountService.update(Mockito.eq(99L), Mockito.any()))
                .thenThrow(new RuntimeException("Không tìm thấy"));

        mockMvc.perform(put("/api/account/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new AccountDTO())))
                .andDo(result -> {
                    System.out.println(">>> Status: " + result.getResponse().getStatus());
                    System.out.println(">>> Body: " + result.getResponse().getContentAsString());
                })
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Không tìm thấy"));
    }

    @Test
    void deleteAccount_success() throws Exception {
        mockMvc.perform(delete("/api/account/1"))
                .andExpect(status().isOk())
                .andDo(result -> {
                    System.out.println(">>> Status: " + result.getResponse().getStatus());
                    System.out.println(">>> Body: " + result.getResponse().getContentAsString());
                })
                .andExpect(content().string(Matchers.containsString("Xóa thành công!")));
    }

    @Test
    void deleteAccount_fail() throws Exception {
        Mockito.doThrow(new RuntimeException("Không tìm thấy"))
                .when(accountService).delete(99L);

        mockMvc.perform(delete("/api/account/99"))
                .andDo(result -> {
                    System.out.println(">>> Status: " + result.getResponse().getStatus());
                    System.out.println(">>> Body: " + result.getResponse().getContentAsString());
                })
                .andExpect(status().isBadRequest());
    }

    @Test
    void getEmail_success() throws Exception {
        Account acc = new Account();
        acc.setAccountId(1L);
        acc.setEmail("test@mail.com");

        Mockito.when(accountRepository.findById(1L)).thenReturn(Optional.of(acc));

        mockMvc.perform(get("/api/account/1/email"))
                .andExpect(status().isOk())
                .andDo(result -> {
                    System.out.println(">>> Status: " + result.getResponse().getStatus());
                    System.out.println(">>> Body: " + result.getResponse().getContentAsString());
                })
                .andExpect(content().string("test@mail.com"));
    }

    @Test
    void getEmail_fail() throws Exception {
        Mockito.when(accountRepository.findById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/account/99/email"))
                .andDo(result -> {
                    System.out.println(">>> Status: " + result.getResponse().getStatus());
                    System.out.println(">>> Body: " + result.getResponse().getContentAsString());
                })
                .andExpect(status().isBadRequest());
    }

}