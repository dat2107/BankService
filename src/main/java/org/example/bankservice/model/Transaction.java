package org.example.bankservice.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "transaction")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long transactionId;

    @ManyToOne
    @JoinColumn(name = "from_card_id", nullable = true, foreignKey = @ForeignKey(name = "fk_transaction_from_card"))
    @OnDelete(action = OnDeleteAction.NO_ACTION) // Hibernate-level, nhưng DB mới quan trọng
    private Card fromCard;

    @ManyToOne
    @JoinColumn(name = "to_card_id", nullable = true, foreignKey = @ForeignKey(name = "fk_transaction_to_card"))
    @OnDelete(action = OnDeleteAction.NO_ACTION)
    private Card toCard;


    @Column(nullable = false)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionStatus status;

    private LocalDateTime createdAt;

    public enum TransactionStatus {
        PENDING,        // mới tạo, chờ nhập OTP
        WAITING_APPROVAL,
        SUCCESS,        // admin duyệt thành công
        FAILED          // OTP sai/hết hạn hoặc admin từ chối
    }

    public enum TransactionType{
        DEPOSIT,WITHDRAW,TRANSFER
    }
}
