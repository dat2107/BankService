package org.example.bankservice.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class UserLevelDTO {
    private String levelName;
    private Integer cardLimit;
    private Double dailyTransferLimit;
}
