package org.example.bankservice.dto;

import lombok.Data;
import java.util.List;

@Data
public class AccountResponseDTO {
    private Long accountId;
    private String customerName;
    private String email;
    private String phoneNumber;
    private BalanceDTO balance;
    private List<CardDTO> cards;
    private UserLevelDTO userLevel;
}
