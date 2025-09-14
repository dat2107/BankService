package org.example.bankservice.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Data
@Getter
@Setter
public class UserLevelDTO {
    private String levelName;
    private Integer cardLimit;
    private BigDecimal dailyTransferLimit;
}
