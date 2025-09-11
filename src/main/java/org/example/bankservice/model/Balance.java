package org.example.bankservice.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "balance")
public class Balance {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long balanceId;

    @OneToOne
    @JoinColumn(name = "account_id", referencedColumnName = "account_id", nullable = false)
    @JsonBackReference
    private Account account;

    @Column(precision = 18, scale = 2)
    private BigDecimal availableBalance;
    @Column(precision = 18, scale = 2)
    private BigDecimal holdBalance;

    private LocalDateTime lastUpdated;
}
