package org.example.bankservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransactionDTO {
    private Long transactionId;
    private String fromCardNumber;
    private String toCardNumber;
    private String type;
    private BigDecimal amount;
    private String status;
    private LocalDateTime createdAt;
}
