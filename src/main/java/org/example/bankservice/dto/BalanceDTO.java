package org.example.bankservice.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class BalanceDTO {
    private Long balanceId;          // ID của balance (nếu FE cần hiển thị)
    private Long accountId;          // ID của account liên kết
    private BigDecimal availableBalance;
    private BigDecimal holdBalance;
    private LocalDateTime lastUpdated;

}
