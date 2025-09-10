package org.example.bankservice.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class BalanceDTO {
    private Long accountId;
    private BigDecimal availableBalance;
    private BigDecimal holdBalance;

}
