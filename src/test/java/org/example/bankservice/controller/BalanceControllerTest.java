package org.example.bankservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.bankservice.config.SecurityConfig;
import org.example.bankservice.dto.BalanceDTO;
import org.example.bankservice.model.Balance;
import org.example.bankservice.security.JwtUtil;
import org.example.bankservice.service.BalanceService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BalanceController.class)
@Import(SecurityConfig.class)
class BalanceControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @MockitoBean private BalanceService balanceService;
    @MockitoBean private JwtUtil jwtUtil;
    @MockitoBean private UserDetailsService userDetailsService;

    // GET /api/balance/{accountId}
    @Test
    void getBalance_success() throws Exception {
        Balance balance = new Balance();
        balance.setBalanceId(1L);
        balance.setAvailableBalance(new BigDecimal("1000"));

        BalanceDTO dto = new BalanceDTO();
        dto.setBalanceId(1L);
        dto.setAvailableBalance(new BigDecimal("1000"));

        Mockito.when(balanceService.getBalance(1L)).thenReturn(balance);
        Mockito.when(balanceService.mapToDTO(balance)).thenReturn(dto);

        mockMvc.perform(get("/api/balance/1"))
                .andDo(result -> {
                    System.out.println(">>> Status: " + result.getResponse().getStatus());
                    System.out.println(">>> Body: " + result.getResponse().getContentAsString());
                })
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.balanceId").value(1))
                .andExpect(jsonPath("$.availableBalance").value(1000));
    }

    @Test
    void getBalance_fail() throws Exception {
        Mockito.when(balanceService.getBalance(99L))
                .thenThrow(new RuntimeException("Không tìm thấy"));

        mockMvc.perform(get("/api/balance/99"))
                .andDo(result -> {
                    System.out.println(">>> Status: " + result.getResponse().getStatus());
                    System.out.println(">>> Body: " + result.getResponse().getContentAsString());
                })
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Không tìm thấy"));
    }

    // POST /api/balance/{accountId}/deposit
    @Test
    void deposit_success() throws Exception {
        Balance balance = new Balance();
        balance.setBalanceId(1L);
        balance.setAvailableBalance(new BigDecimal("1500"));

        BalanceDTO dto = new BalanceDTO();
        dto.setBalanceId(1L);
        dto.setAvailableBalance(new BigDecimal("1500"));

        Mockito.when(balanceService.deposit(1L, new BigDecimal("500"), 10L))
                .thenReturn(balance);
        Mockito.when(balanceService.mapToDTO(balance)).thenReturn(dto);

        mockMvc.perform(post("/api/balance/1/deposit")
                        .param("amount", "500")
                        .param("toCardId", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(result -> {
                    System.out.println(">>> Status: " + result.getResponse().getStatus());
                    System.out.println(">>> Body: " + result.getResponse().getContentAsString());
                })
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.availableBalance").value(1500));
    }

    @Test
    void deposit_fail() throws Exception {
        Mockito.when(balanceService.deposit(1L, new BigDecimal("500"), 10L))
                .thenThrow(new RuntimeException("Nạp tiền thất bại"));

        mockMvc.perform(post("/api/balance/1/deposit")
                        .param("amount", "500")
                        .param("toCardId", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(result -> {
                    System.out.println(">>> Status: " + result.getResponse().getStatus());
                    System.out.println(">>> Body: " + result.getResponse().getContentAsString());
                })
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Nạp tiền thất bại"));
    }

    // POST /api/balance/{accountId}/withdraw
    @Test
    void withdraw_success() throws Exception {
        Balance balance = new Balance();
        balance.setBalanceId(1L);
        balance.setAvailableBalance(new BigDecimal("800"));

        BalanceDTO dto = new BalanceDTO();
        dto.setBalanceId(1L);
        dto.setAvailableBalance(new BigDecimal("800"));

        Mockito.when(balanceService.withdraw(1L, new BigDecimal("200"), 20L))
                .thenReturn(balance);
        Mockito.when(balanceService.mapToDTO(balance)).thenReturn(dto);

        mockMvc.perform(post("/api/balance/1/withdraw")
                        .param("amount", "200")
                        .param("fromCardId", "20")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(result -> {
                    System.out.println(">>> Status: " + result.getResponse().getStatus());
                    System.out.println(">>> Body: " + result.getResponse().getContentAsString());
                })
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.availableBalance").value(800));
    }

    @Test
    void withdraw_fail() throws Exception {
        Mockito.when(balanceService.withdraw(1L, new BigDecimal("200"), 20L))
                .thenThrow(new RuntimeException("Số dư không đủ"));

        mockMvc.perform(post("/api/balance/1/withdraw")
                        .param("amount", "200")
                        .param("fromCardId", "20")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(result -> {
                    System.out.println(">>> Status: " + result.getResponse().getStatus());
                    System.out.println(">>> Body: " + result.getResponse().getContentAsString());
                })
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Số dư không đủ"));
    }
}

