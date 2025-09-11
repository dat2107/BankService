package org.example.bankservice.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class CardResponseDTO {
    private Long cardId;
    private String cardNumber;
    private String cardType;
    private String status;
    private LocalDate expiryDate;

    private AccountResponseDTO account;
}
