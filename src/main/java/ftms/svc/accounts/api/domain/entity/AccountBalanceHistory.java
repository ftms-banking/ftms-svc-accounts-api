package ftms.svc.accounts.api.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Account Balance History entity for tracking balance changes
 * Maps to 'account_balance_history' table in ftms_db
 */
@Entity
@Table(name = "account_balance_history")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountBalanceHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false)
    private FtmsAccount account;

    @Column(name = "previous_balance", nullable = false, precision = 19, scale = 4)
    private BigDecimal previousBalance;

    @Column(name = "new_balance", nullable = false, precision = 19, scale = 4)
    private BigDecimal newBalance;

    @Column(name = "change_amount", nullable = false, precision = 19, scale = 4)
    private BigDecimal changeAmount;

    @Column(name = "change_reason", nullable = false, length = 100)
    private String changeReason;

    @Column(name = "transaction_id", length = 36)
    private String transactionId;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // Lifecycle callback
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    // Helper methods
    public boolean isCredit() {
        return changeAmount.compareTo(BigDecimal.ZERO) > 0;
    }

    public boolean isDebit() {
        return changeAmount.compareTo(BigDecimal.ZERO) < 0;
    }

    public BigDecimal getAbsoluteChangeAmount() {
        return changeAmount.abs();
    }

    public String getChangeType() {
        return isCredit() ? "CREDIT" : "DEBIT";
    }

    @Override
    public String toString() {
        return "AccountBalanceHistory{" +
                "id=" + id +
                ", previousBalance=" + previousBalance +
                ", newBalance=" + newBalance +
                ", changeAmount=" + changeAmount +
                ", changeReason='" + changeReason + '\'' +
                ", transactionId='" + transactionId + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}