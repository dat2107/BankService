package org.example.bankservice.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class TransferDTO {
    private Long fromCardId;     // thẻ nguồn
    private String toCardNumber; // số thẻ nhận
    private BigDecimal amount;   // số tiền
    private String email;
}
