package org.example.bankservice.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Getter
@Setter
@Table(name = "user_level")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class UserLevel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "level_name", nullable = false, unique = true)
    private String levelName;

    @Column(name = "card_limit", nullable = false)
    private Integer cardLimit;

    @Column(name = "daily_transfer_limit", nullable = false)
    private BigDecimal dailyTransferLimit;


}
