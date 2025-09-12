package org.example.bankservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.bankservice.config.SecurityConfig;
import org.example.bankservice.dto.OtpConfirmDTO;
import org.example.bankservice.dto.TransactionDTO;
import org.example.bankservice.dto.TransferDTO;
import org.example.bankservice.model.Transaction;
import org.example.bankservice.security.JwtUtil;
import org.example.bankservice.service.TransferService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TransferController.class)
@Import(SecurityConfig.class)
class TransferControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @MockitoBean
    private TransferService transferService;

    @MockitoBean
    private JwtUtil jwtUtil;

    @MockitoBean
    private UserDetailsService userDetailsService;

    // --- Test requestTransfer thành công ---
    @Test
    @WithMockUser(username = "user1", roles = {"USER"})
    void requestTransfer_success() throws Exception {
        TransferDTO dto = new TransferDTO();
        dto.setFromCardId(1L);
        dto.setToCardNumber("4111111111111111");
        dto.setAmount(BigDecimal.valueOf(500));

        Transaction tx = new Transaction();
        tx.setTransactionId(100L);

        Mockito.when(transferService.createTransferRequest(Mockito.any(TransferDTO.class)))
                .thenReturn(tx);

        mockMvc.perform(post("/api/transfer/request")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andDo(r -> {
                    System.out.println(">>> Status: " + r.getResponse().getStatus());
                    System.out.println(">>> Body: " + r.getResponse().getContentAsString());
                })
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.transactionId").value(100))
                .andExpect(jsonPath("$.message").value(containsString("Mã OTP")));
    }

    // --- Test requestTransfer thất bại ---
    @Test
    @WithMockUser(username = "user1", roles = {"USER"})
    void requestTransfer_fail() throws Exception {
        Mockito.when(transferService.createTransferRequest(Mockito.any(TransferDTO.class)))
                .thenThrow(new RuntimeException("Không tìm thấy tài khoản"));

        mockMvc.perform(post("/api/transfer/request")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new TransferDTO())))
                .andDo(r -> {
                    System.out.println(">>> Status: " + r.getResponse().getStatus());
                    System.out.println(">>> Body: " + r.getResponse().getContentAsString());
                })
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Không tìm thấy tài khoản"));
    }

    // --- Test confirmOtp thành công ---
    @Test
    @WithMockUser(username = "user1", roles = {"USER"})
    void confirmOtp_success() throws Exception {
        OtpConfirmDTO dto = new OtpConfirmDTO();
        dto.setTransactionId(100L);
        dto.setOtp("123456");

        TransactionDTO tx = new TransactionDTO();
        tx.setTransactionId(100L);
        tx.setStatus("SUCCESS");

        Mockito.when(transferService.confirmOtp(Mockito.any(OtpConfirmDTO.class)))
                .thenReturn(tx);

        mockMvc.perform(post("/api/transfer/confirm")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andDo(r -> {
                    System.out.println(">>> Status: " + r.getResponse().getStatus());
                    System.out.println(">>> Body: " + r.getResponse().getContentAsString());
                })
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.transactionId").value(100))
                .andExpect(jsonPath("$.status").value("SUCCESS"));
    }

    // --- Test confirmOtp thất bại ---
    @Test
    @WithMockUser(username = "user1", roles = {"USER"})
    void confirmOtp_fail() throws Exception {
        Mockito.when(transferService.confirmOtp(Mockito.any(OtpConfirmDTO.class)))
                .thenThrow(new RuntimeException("OTP sai"));

        mockMvc.perform(post("/api/transfer/confirm")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new OtpConfirmDTO())))
                .andDo(r -> {
                    System.out.println(">>> Status: " + r.getResponse().getStatus());
                    System.out.println(">>> Body: " + r.getResponse().getContentAsString());
                })
                .andExpect(status().isBadRequest())
                .andExpect(content().string("OTP sai"));
    }
}
