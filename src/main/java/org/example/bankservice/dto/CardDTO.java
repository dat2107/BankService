package org.example.bankservice.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import org.example.bankservice.model.Card;

import java.time.LocalDate;

@Data
public class CardDTO {
    private Long accountId;
    private Card.Type cardtype;
    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate expiryDate;
    private Card.Status status;

    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    public Card.Type getCardtype() {
        return cardtype;
    }

    public void setCardtype(Card.Type cardtype) {
        this.cardtype = cardtype;
    }

    public LocalDate getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(LocalDate expiryDate) {
        this.expiryDate = expiryDate;
    }

    public Card.Status getStatus() {
        return status;
    }

    public void setStatus(Card.Status status) {
        this.status = status;
    }
}
