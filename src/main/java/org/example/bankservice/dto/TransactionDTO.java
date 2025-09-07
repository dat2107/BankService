package org.example.bankservice.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class TransactionDTO {
    private Long transactionId;
    private String fromCardNumber;
    private String toCardNumber;
    private BigDecimal amount;
    private String status;
    private LocalDateTime createdAt;
}
