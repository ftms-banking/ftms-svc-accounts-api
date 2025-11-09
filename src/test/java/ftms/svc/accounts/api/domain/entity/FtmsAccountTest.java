package ftms.svc.accounts.api.domain.entity;

import ftms.svc.accounts.api.constants.FtmsAccountType;
import ftms.svc.accounts.api.domain.FtmsAccountStatus;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class FtmsAccountTest {

    @Test
    void onCreate_setsTimestampsAndDefaults_whenFieldsNull() {
        // Arrange: leave fields that should be defaulted as null
        FtmsAccount acc = FtmsAccount.builder()
                .customerId("550e8400-e29b-41d4-a716-446655440002")
                .accountNumber("12345678901234567890")
                .accountType(FtmsAccountType.SAVINGS)
                // balance, availableBalance, status, currency, openedAt -> null on purpose
                .build();

        // Act
        acc.onCreate();

        // Assert: timestamps set
        assertThat(acc.getCreatedAt()).isNotNull();
        assertThat(acc.getUpdatedAt()).isNotNull();
        assertThat(acc.getOpenedAt()).isNotNull();

        // Assert: defaults applied
        assertThat(acc.getBalance()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(acc.getAvailableBalance()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(acc.getStatus()).isEqualTo(FtmsAccountStatus.PENDING);
        assertThat(acc.getCurrency()).isEqualTo("USD");

        // Not touched fields remain as set
        assertThat(acc.getCustomerId()).isEqualTo("550e8400-e29b-41d4-a716-446655440002");
        assertThat(acc.getAccountNumber()).isEqualTo("12345678901234567890");
        assertThat(acc.getAccountType()).isEqualTo(FtmsAccountType.SAVINGS);
    }

    @Test
    void onCreate_doesNotOverwriteProvidedValues() {
        LocalDateTime opened = LocalDateTime.now().minusDays(1);
        LocalDateTime created = LocalDateTime.now().minusDays(2);
        LocalDateTime updated = LocalDateTime.now().minusDays(1);

        FtmsAccount acc = FtmsAccount.builder()
                .customerId("C1")
                .accountNumber("11112222333344445555")
                .accountType(FtmsAccountType.BUSINESS)
                .balance(new BigDecimal("10.50"))
                .availableBalance(new BigDecimal("9.25"))
                .status(FtmsAccountStatus.ACTIVE)
                .currency("INR")
                .openedAt(opened)
                .createdAt(created)
                .updatedAt(updated)
                .build();

        // Act
        acc.onCreate();

        // Assert: your code sets createdAt, updatedAt, openedAt on create unconditionally.
        // So we assert they are updated to "now-ish" (not null and >= previous).
        assertThat(acc.getCreatedAt()).isAfterOrEqualTo(created);
        assertThat(acc.getUpdatedAt()).isAfterOrEqualTo(updated);
        assertThat(acc.getOpenedAt()).isAfterOrEqualTo(opened);

        // Assert: provided non-null business fields are preserved
        assertThat(acc.getBalance()).isEqualByComparingTo("10.50");
        assertThat(acc.getAvailableBalance()).isEqualByComparingTo("9.25");
        assertThat(acc.getStatus()).isEqualTo(FtmsAccountStatus.ACTIVE);
        assertThat(acc.getCurrency()).isEqualTo("INR");
        assertThat(acc.getAccountType()).isEqualTo(FtmsAccountType.BUSINESS);
    }

    @Test
    void onUpdate_refreshesOnlyUpdatedAt() {
        FtmsAccount acc = FtmsAccount.builder()
                .customerId("C2")
                .accountNumber("99998888777766665555")
                .accountType(FtmsAccountType.CHECKING)
                .build();

        // Simulate persisted entity
        acc.onCreate();
        LocalDateTime createdAt = acc.getCreatedAt();
        LocalDateTime prevUpdated = acc.getUpdatedAt();

        // Act
        acc.onUpdate();

        // Assert
        assertThat(acc.getCreatedAt()).isEqualTo(createdAt);           // unchanged
        assertThat(acc.getUpdatedAt()).isAfterOrEqualTo(prevUpdated);  // refreshed
    }

    @Test
    void balanceHistory_isEmptyMutableListByDefault() {
        FtmsAccount acc = FtmsAccount.builder()
                .customerId("C3")
                .accountNumber("00001111222233334444")
                .accountType(FtmsAccountType.SAVINGS)
                .build();

        assertThat(acc.getBalanceHistory()).isNotNull().isEmpty();

        // Ensure list is mutable (Builder.Default created an ArrayList)
        AccountBalanceHistory hist = AccountBalanceHistory.builder()
                .previousBalance(new BigDecimal("0"))
                .newBalance(new BigDecimal("5"))
                .changeAmount(new BigDecimal("5"))
                .changeReason("init")
                .build();

        acc.getBalanceHistory().add(hist);
        assertThat(acc.getBalanceHistory()).hasSize(1);
    }

    @Test
    void defaultsApplied_independently() {
        // Only some are null
        FtmsAccount acc = FtmsAccount.builder()
                .customerId("C4")
                .accountNumber("12345000000000000000")
                .accountType(FtmsAccountType.SAVINGS)
                .balance(new BigDecimal("1.00")) // set
                // availableBalance, status, currency are null
                .build();

        acc.onCreate();

        // Provided balance stays; missing ones default
        assertThat(acc.getBalance()).isEqualByComparingTo("1.00");
        assertThat(acc.getAvailableBalance()).isEqualByComparingTo("0.00");
        assertThat(acc.getStatus()).isEqualTo(FtmsAccountStatus.PENDING);
        assertThat(acc.getCurrency()).isEqualTo("USD");
    }
}
