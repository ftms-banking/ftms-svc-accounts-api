package ftms.svc.accounts.api.domain.entity;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class AccountBalanceHistoryTest {

    @Test
    void prePersist_setsCreatedAt() {
        // Arrange
        AccountBalanceHistory hist = AccountBalanceHistory.builder()
                .previousBalance(new BigDecimal("100.00"))
                .newBalance(new BigDecimal("120.00"))
                .changeAmount(new BigDecimal("20.00"))
                .changeReason("Initial credit")
                .transactionId("tx-123")
                // .account(...)  // not needed for unit test (DB constraint applies only when persisting)
                .build();

        // Act — call lifecycle callback directly (no DB/JPA needed)
        hist.onCreate();

        // Assert
        assertThat(hist.getCreatedAt()).isNotNull();
        // Optional: make sure it's "now-ish"
        assertThat(hist.getCreatedAt()).isBeforeOrEqualTo(LocalDateTime.now());
    }

    @Test
    void isCredit_returnsTrue_whenChangeAmountPositive() {
        AccountBalanceHistory hist = AccountBalanceHistory.builder()
                .changeAmount(new BigDecimal("0.01"))
                .build();

        assertThat(hist.isCredit()).isTrue();
        assertThat(hist.isDebit()).isFalse();
        assertThat(hist.getChangeType()).isEqualTo("CREDIT");
    }

    @Test
    void isDebit_returnsTrue_whenChangeAmountNegative() {
        AccountBalanceHistory hist = AccountBalanceHistory.builder()
                .changeAmount(new BigDecimal("-5.00"))
                .build();

        assertThat(hist.isDebit()).isTrue();
        assertThat(hist.isCredit()).isFalse();
        assertThat(hist.getChangeType()).isEqualTo("DEBIT");
    }

    @Test
    void getAbsoluteChangeAmount_returnsAbsoluteValue() {
        AccountBalanceHistory hist = AccountBalanceHistory.builder()
                .changeAmount(new BigDecimal("-123.4567"))
                .build();

        assertThat(hist.getAbsoluteChangeAmount()).isEqualByComparingTo("123.4567");
    }

    @Test
    void changeType_isDebit_whenZero_changeAmount() {
        // NOTE: with your current code, zero is NOT credit (isCredit=false) → falls back to "DEBIT"
        AccountBalanceHistory hist = AccountBalanceHistory.builder()
                .changeAmount(BigDecimal.ZERO)
                .build();

        assertThat(hist.isCredit()).isFalse();
        assertThat(hist.isDebit()).isFalse(); // - compareTo(0) < 0 is false for 0
        assertThat(hist.getChangeType()).isEqualTo("DEBIT"); // because isCredit() ? "CREDIT" : "DEBIT"
    }

    @Test
    void toString_containsKeyFields_but_not_nullSafeOnAccount() {
        AccountBalanceHistory hist = AccountBalanceHistory.builder()
                .id(1L)
                .previousBalance(new BigDecimal("10.00"))
                .newBalance(new BigDecimal("15.00"))
                .changeAmount(new BigDecimal("5.00"))
                .changeReason("test")
                .transactionId("tx-abc")
                .createdAt(LocalDateTime.now())
                .build();

        String s = hist.toString();
        assertThat(s).contains("id=1");
        assertThat(s).contains("previousBalance=10.00");
        assertThat(s).contains("newBalance=15.00");
        assertThat(s).contains("changeAmount=5.00");
        assertThat(s).contains("changeReason='test'");
        assertThat(s).contains("transactionId='tx-abc'");
    }
}
