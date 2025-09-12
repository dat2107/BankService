package org.example.bankservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.bankservice.config.SecurityConfig;
import org.example.bankservice.dto.CardDTO;
import org.example.bankservice.dto.CardResponseDTO;
import org.example.bankservice.security.JwtUtil;
import org.example.bankservice.service.CardService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CardController.class)
@Import(SecurityConfig.class)
class CardControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @MockitoBean
    private CardService cardService;
    @MockitoBean
    private JwtUtil jwtUtil;

    @MockitoBean
    private UserDetailsService userDetailsService;


    // POST /api/card
    @Test
    void createCard_success() throws Exception {
        CardDTO dto = new CardDTO();
        dto.setAccountId(1L);

        CardResponseDTO resp = new CardResponseDTO();
        resp.setCardId(10L);
        resp.setCardNumber("411111******1111");

        Mockito.when(cardService.create(Mockito.any(CardDTO.class), Mockito.anyString()))
                .thenReturn(resp);

        mockMvc.perform(post("/api/card")
                        .header("Authorization", "Bearer fake-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andDo(result -> {
                    System.out.println(">>> Status: " + result.getResponse().getStatus());
                    System.out.println(">>> Body: " + result.getResponse().getContentAsString());
                })
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cardId").value(10));
    }

    @Test
    void createCard_fail() throws Exception {
        Mockito.when(cardService.create(Mockito.any(CardDTO.class), Mockito.anyString()))
                .thenThrow(new RuntimeException("Account không tồn tại"));


        mockMvc.perform(post("/api/card")
                        .header("Authorization", "Bearer fake-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new CardDTO())))
                .andDo(result -> {
                    System.out.println(">>> Status: " + result.getResponse().getStatus());
                    System.out.println(">>> Body: " + result.getResponse().getContentAsString());
                })
                .andExpect(status().isBadRequest());
    }

    // GET /api/card/{id}
    @Test
    void getCard_success() throws Exception {
        CardResponseDTO resp = new CardResponseDTO();
        resp.setCardId(10L);
        resp.setCardNumber("4111");

        Mockito.when(cardService.getById(10L)).thenReturn(resp);

        mockMvc.perform(get("/api/card/10"))
                .andExpect(status().isOk())
                .andDo(result -> {
                    System.out.println(">>> Status: " + result.getResponse().getStatus());
                    System.out.println(">>> Body: " + result.getResponse().getContentAsString());
                })
                .andExpect(jsonPath("$.cardId").value(10));
    }

    @Test
    void getCard_notFound() throws Exception {
        Mockito.when(cardService.getById(99L))
                .thenThrow(new RuntimeException("Không tìm thấy"));

        mockMvc.perform(get("/api/card/99"))
                .andDo(result -> {
                    System.out.println(">>> Status: " + result.getResponse().getStatus());
                    System.out.println(">>> Body: " + result.getResponse().getContentAsString());
                })
                .andExpect(status().isBadRequest());
    }

    // DELETE /api/card/{id}
    @Test
    void deleteCard_success() throws Exception {
        mockMvc.perform(delete("/api/card/10"))
                .andDo(result -> {
                    System.out.println(">>> Status: " + result.getResponse().getStatus());
                    System.out.println(">>> Body: " + result.getResponse().getContentAsString());
                })
                .andExpect(status().isOk());
    }

    @Test
    void deleteCard_fail() throws Exception {
        Mockito.doThrow(new RuntimeException("Không tìm thấy"))
                .when(cardService).deleteCard(99L);

        mockMvc.perform(delete("/api/card/99"))
                .andDo(result -> {
                    System.out.println(">>> Status: " + result.getResponse().getStatus());
                    System.out.println(">>> Body: " + result.getResponse().getContentAsString());
                })
                .andExpect(status().isBadRequest());
    }

    // GET /api/card
    @Test
    void getAllCards_success() throws Exception {
        CardResponseDTO c1 = new CardResponseDTO();
        c1.setCardId(1L);
        c1.setCardNumber("1111");

        CardResponseDTO c2 = new CardResponseDTO();
        c2.setCardId(2L);
        c2.setCardNumber("2222");

        Mockito.when(cardService.getAllCard()).thenReturn(java.util.List.of(c1, c2));

        mockMvc.perform(get("/api/card"))
                .andExpect(status().isOk())
                .andDo(result -> {
                    System.out.println(">>> Status: " + result.getResponse().getStatus());
                    System.out.println(">>> Body: " + result.getResponse().getContentAsString());
                })
                .andExpect(jsonPath("$[0].cardId").value(1))
                .andExpect(jsonPath("$[1].cardId").value(2));
    }

    @Test
    void getAllCards_fail() throws Exception {
        Mockito.when(cardService.getAllCard())
                .thenThrow(new RuntimeException("DB error"));

        mockMvc.perform(get("/api/card"))
                .andDo(result -> {
                    System.out.println(">>> Status: " + result.getResponse().getStatus());
                    System.out.println(">>> Body: " + result.getResponse().getContentAsString());
                })
                .andExpect(status().isBadRequest())
                .andExpect(content().string("DB error"));
    }

    // GET /api/card/account/{accountId}
    @Test
    void getCardsByAccount_success() throws Exception {
        CardResponseDTO card = new CardResponseDTO();
        card.setCardId(5L);
        card.setCardNumber("5555");

        Mockito.when(cardService.getByAccountId(1L))
                .thenReturn(java.util.List.of(card));

        mockMvc.perform(get("/api/card/account/1"))
                .andExpect(status().isOk())
                .andDo(result -> {
                    System.out.println(">>> Status: " + result.getResponse().getStatus());
                    System.out.println(">>> Body: " + result.getResponse().getContentAsString());
                })
                .andExpect(jsonPath("$[0].cardId").value(5));
    }

    @Test
    void getCardsByAccount_empty() throws Exception {
        Mockito.when(cardService.getByAccountId(2L))
                .thenReturn(java.util.List.of());

        mockMvc.perform(get("/api/card/account/2"))
                .andExpect(status().isOk())
                .andDo(result -> {
                    System.out.println(">>> Status: " + result.getResponse().getStatus());
                    System.out.println(">>> Body: " + result.getResponse().getContentAsString());
                })
                .andExpect(content().string("Không có thẻ nào cho accountId = 2"));
    }

    // GET /api/card/number/{cardNumber}
    @Test
    void getByCardNumber_success() throws Exception {
        CardResponseDTO resp = new CardResponseDTO();
        resp.setCardId(99L);
        resp.setCardNumber("9999");

        Mockito.when(cardService.getByCardNumber("9999")).thenReturn(resp);

        mockMvc.perform(get("/api/card/number/9999"))
                .andExpect(status().isOk())
                .andDo(result -> {
                    System.out.println(">>> Status: " + result.getResponse().getStatus());
                    System.out.println(">>> Body: " + result.getResponse().getContentAsString());
                })
                .andExpect(jsonPath("$.cardId").value(99));
    }

    @Test
    void getByCardNumber_fail() throws Exception {
        Mockito.when(cardService.getByCardNumber("0000"))
                .thenThrow(new RuntimeException("Không tìm thấy"));

        mockMvc.perform(get("/api/card/number/0000"))
                .andDo(result -> {
                    System.out.println(">>> Status: " + result.getResponse().getStatus());
                    System.out.println(">>> Body: " + result.getResponse().getContentAsString());
                })
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Không tìm thấy"));
    }

    // PUT /api/card/{id}/status
    @Test
    void updateStatus_success() throws Exception {
        CardResponseDTO resp = new CardResponseDTO();
        resp.setCardId(77L);
        resp.setCardNumber("7777");

        Mockito.when(cardService.updateStatus(77L)).thenReturn(resp);

        mockMvc.perform(put("/api/card/77/status"))
                .andDo(result -> {
                    System.out.println(">>> Status: " + result.getResponse().getStatus());
                    System.out.println(">>> Body: " + result.getResponse().getContentAsString());
                })
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cardId").value(77));
    }

    @Test
    void updateStatus_fail() throws Exception {
        Mockito.when(cardService.updateStatus(88L))
                .thenThrow(new RuntimeException("Không tìm thấy"));

        mockMvc.perform(put("/api/card/88/status"))
                .andDo(result -> {
                    System.out.println(">>> Status: " + result.getResponse().getStatus());
                    System.out.println(">>> Body: " + result.getResponse().getContentAsString());
                })
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Không tìm thấy"));
    }

}
