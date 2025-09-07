package org.example.bankservice.dto;

import lombok.Data;

@Data
public class OtpConfirmDTO {
    private Long transactionId;
    private String otp;
}
