package org.example.bankservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.bankservice.config.SecurityConfig;
import org.example.bankservice.dto.TransactionDTO;
import org.example.bankservice.model.Transaction;
import org.example.bankservice.repository.TransactionRepository;
import org.example.bankservice.security.JwtUtil;
import org.example.bankservice.service.TransactionService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TransactionController.class)
@Import(SecurityConfig.class)
class TransactionControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @MockitoBean private JwtUtil jwtUtil;
    @MockitoBean private TransactionService transactionService;
    @MockitoBean private TransactionRepository transactionRepo;
    @MockitoBean private UserDetailsService userDetailsService;

    // --- GET /api/transaction ---
    @Test
    @WithMockUser(roles = "ADMIN")
    void getAll_success() throws Exception {
        Mockito.when(transactionService.getAll("SUCCESS", 0, 7))
                .thenReturn(new PageImpl<>(List.of(new TransactionDTO())));

        mockMvc.perform(get("/api/transaction")
                        .param("status", "SUCCESS"))
                .andDo(result -> {
                    System.out.println(">>> Status: " + result.getResponse().getStatus());
                    System.out.println(">>> Body: " + result.getResponse().getContentAsString());
                })
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    @WithMockUser(roles = "USER") // ❌ không phải ADMIN
    void getAll_forbidden() throws Exception {
        mockMvc.perform(get("/api/transaction")
                        .param("status", "SUCCESS"))
                .andDo(result -> {
                    System.out.println(">>> Status: " + result.getResponse().getStatus());
                    System.out.println(">>> Body: " + result.getResponse().getContentAsString());
                })
                .andExpect(status().isForbidden())
                .andExpect(content().string("Bạn không có quyền truy cập tài nguyên này"));
    }

    // --- GET /api/transaction/account/{accountId} ---
    @Test
    void getTransactionsByAccount_success() throws Exception {
        Mockito.when(transactionService.findByAccountId(1L))
                .thenReturn(List.of(new TransactionDTO()));

        mockMvc.perform(get("/api/transaction/account/1"))
                .andDo(result -> {
                    System.out.println(">>> Status: " + result.getResponse().getStatus());
                    System.out.println(">>> Body: " + result.getResponse().getContentAsString());
                })
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void getTransactionsByAccount_fail() throws Exception {
        Mockito.when(transactionService.findByAccountId(99L))
                .thenThrow(new RuntimeException("Account not found"));

        mockMvc.perform(get("/api/transaction/account/99"))
                .andDo(result -> {
                    System.out.println(">>> Status: " + result.getResponse().getStatus());
                    System.out.println(">>> Body: " + result.getResponse().getContentAsString());
                })
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Account not found"));
    }

    // --- GET /api/transaction/card/{cardId} ---
    @Test
    void getByCard_success() throws Exception {
        Mockito.when(transactionService.getByCard(10L, 0, 7))
                .thenReturn(new PageImpl<>(List.of(new TransactionDTO())));

        mockMvc.perform(get("/api/transaction/card/10"))
                .andDo(result -> {
                    System.out.println(">>> Status: " + result.getResponse().getStatus());
                    System.out.println(">>> Body: " + result.getResponse().getContentAsString());
                })
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    void getByCard_fail() throws Exception {
        Mockito.when(transactionService.getByCard(99L, 0, 7))
                .thenThrow(new RuntimeException("Card not found"));

        mockMvc.perform(get("/api/transaction/card/99"))
                .andDo(result -> {
                    System.out.println(">>> Status: " + result.getResponse().getStatus());
                    System.out.println(">>> Body: " + result.getResponse().getContentAsString());
                })
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Card not found"));
    }

    // --- PUT /api/transaction/{id}/status ---
    @Test
    void updateStatus_success() throws Exception {
        TransactionDTO dto = new TransactionDTO();
        dto.setStatus("SUCCESS");

        Mockito.when(transactionService.updateStatus(1L, "SUCCESS")).thenReturn(dto);

        mockMvc.perform(put("/api/transaction/1/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andDo(result -> {
                    System.out.println(">>> Status: " + result.getResponse().getStatus());
                    System.out.println(">>> Body: " + result.getResponse().getContentAsString());
                })
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"));
    }

    @Test
    void updateStatus_fail() throws Exception {
        Mockito.when(transactionService.updateStatus(99L, "FAILED"))
                .thenThrow(new RuntimeException("Not found"));

        TransactionDTO dto = new TransactionDTO();
        dto.setStatus("FAILED");

        mockMvc.perform(put("/api/transaction/99/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andDo(result -> {
                    System.out.println(">>> Status: " + result.getResponse().getStatus());
                    System.out.println(">>> Body: " + result.getResponse().getContentAsString());
                })
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Not found"));
    }

    // --- GET /api/transaction/{id} ---
    @Test
    void getOne_success() throws Exception {
        Transaction tx = new Transaction();
        Mockito.when(transactionRepo.findById(1L)).thenReturn(Optional.of(tx));

        TransactionDTO dto = new TransactionDTO();
        dto.setTransactionId(1L);
        Mockito.when(transactionService.toDto(tx)).thenReturn(dto);

        mockMvc.perform(get("/api/transaction/1"))
                .andDo(result -> {
                    System.out.println(">>> Status: " + result.getResponse().getStatus());
                    System.out.println(">>> Body: " + result.getResponse().getContentAsString());
                })
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.transactionId").value(1));
    }

    @Test
    void getOne_fail() throws Exception {
        Mockito.when(transactionRepo.findById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/transaction/99"))
                .andDo(result -> {
                    System.out.println(">>> Status: " + result.getResponse().getStatus());
                    System.out.println(">>> Body: " + result.getResponse().getContentAsString());
                })
                .andExpect(status().isNotFound());
    }
}
