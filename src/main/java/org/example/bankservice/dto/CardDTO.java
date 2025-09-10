package org.example.bankservice.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.example.bankservice.model.Card;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@Data
public class CardDTO {
    private Long accountId;
    private Long cardId;
    private String cardNumber;
    private Card.Type cardType;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate expiryDate;
    private Card.Status status;
}
