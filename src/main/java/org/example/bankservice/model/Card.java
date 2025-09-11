package org.example.bankservice.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@Table(name = "card")
public class Card {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "card_id")
    private Long cardId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", referencedColumnName = "account_id", nullable = false)
    @JsonIgnoreProperties({"cards"})
//    @JsonBackReference
    private Account account;

    @Column(name = "card_number")
        private String cardNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "card_type", columnDefinition = "ENUM('DEBIT','CREDIT')")
    private Type cardType;

    private LocalDate expiryDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", columnDefinition = "ENUM('ACTIVE','INACTIVE')")
    private Status status = Status.ACTIVE;

    public enum Type{
        DEBIT,CREDIT
    }

    public enum Status{
        ACTIVE,INACTIVE
    }
}
