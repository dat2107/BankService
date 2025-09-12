package org.example.bankservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.bankservice.config.SecurityConfig;
import org.example.bankservice.dto.TransactionDTO;
import org.example.bankservice.model.Transaction;
import org.example.bankservice.security.JwtUtil;
import org.example.bankservice.service.TransactionService;
import org.example.bankservice.service.TransferService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AdminTransactionController.class)
@Import(SecurityConfig.class)
class AdminTransactionControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @MockitoBean
    private TransferService transferService;

    @MockitoBean
    private TransactionService transactionService;
    @MockitoBean
    private JwtUtil jwtUtil;
    @MockitoBean
    private UserDetailsService userDetailsService;

    @Test
    @WithMockUser(roles = "ADMIN")
    void approveTransaction_withAdminRole_success() throws Exception {
        mockMvc.perform(post("/api/admin/transactions/1/approve"))
                .andDo(result -> {
                    System.out.println(">>> Status: " + result.getResponse().getStatus());
                    System.out.println(">>> Body: " + result.getResponse().getContentAsString());
                })
                .andExpect(status().isOk()); // ADMIN -> pass
    }

    @Test
    @WithMockUser(roles = "USER")
    void approveTransaction_withUserRole_forbidden() throws Exception {
        mockMvc.perform(post("/api/admin/transactions/1/approve"))
                .andDo(result -> {
                    System.out.println(">>> Status: " + result.getResponse().getStatus());
                    System.out.println(">>> Body: " + result.getResponse().getContentAsString());
                })
                .andExpect(status().isForbidden()); // USER -> 403
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void reject_withAdminRole_success() throws Exception {
        mockMvc.perform(post("/api/admin/transactions/2/reject"))
                .andDo(result -> {
                    System.out.println(">>> Status: " + result.getResponse().getStatus());
                    System.out.println(">>> Body: " + result.getResponse().getContentAsString());
                })
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "USER")
    void reject_withUserRole_forbidden() throws Exception {
        mockMvc.perform(post("/api/admin/transactions/2/reject"))
                .andDo(result -> {
                    System.out.println(">>> Status: " + result.getResponse().getStatus());
                    System.out.println(">>> Body: " + result.getResponse().getContentAsString());
                })
                .andExpect(status().isForbidden());
    }
}
