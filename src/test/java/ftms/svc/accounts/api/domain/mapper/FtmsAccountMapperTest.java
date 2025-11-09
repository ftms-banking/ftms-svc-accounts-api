package ftms.svc.accounts.api.domain.mapper;

import ftms.svc.accounts.api.constants.FtmsAccountType;
import ftms.svc.accounts.api.domain.FtmsAccountResult;
import ftms.svc.accounts.api.domain.FtmsAccountStatus;
import ftms.svc.accounts.api.domain.FtmsCreateAccount;
import ftms.svc.accounts.api.domain.entity.FtmsAccount;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class FtmsAccountMapperTest {

    @Test
    void toFtmsAccountResult_mapsAllFields() {
        // Arrange (given an entity from DB)
        LocalDateTime now = LocalDateTime.now();

        FtmsAccount entity = FtmsAccount.builder()
                .uuid("bb02fb66-a82e-48fb-903a-632ddc664a5a")
                .customerId("550e8400-e29b-41d4-a716-446655440002")
                .accountType(FtmsAccountType.SAVINGS)
                .currency("USD")
                .balance(new BigDecimal("123.45"))
                .status(FtmsAccountStatus.ACTIVE)
                .createdAt(now.minusDays(1))
                .updatedAt(now)
                .build();

        // Act
        FtmsAccountResult dto = FtmsAccountMapper.toFtmsAccountResult(entity);

        // Assert (field-by-field to catch any missed mapping)
        assertThat(dto.getUuid()).isEqualTo("bb02fb66-a82e-48fb-903a-632ddc664a5a");
        assertThat(dto.getCustomerId()).isEqualTo("550e8400-e29b-41d4-a716-446655440002");
        assertThat(dto.getAccountType()).isEqualTo(FtmsAccountType.SAVINGS);
        assertThat(dto.getCurrency()).isEqualTo("USD");
        assertThat(dto.getBalance()).isEqualByComparingTo("123.45");
        assertThat(dto.getStatus()).isEqualTo(FtmsAccountStatus.ACTIVE);
        assertThat(dto.getCreatedAt()).isEqualTo(now.minusDays(1));
        assertThat(dto.getUpdatedAt()).isEqualTo(now);
    }

    @Test
    void toFtmsAccount_mapsCreateRequestToEntity_minimumFields() {
        // Arrange (incoming request)
        FtmsCreateAccount create = new FtmsCreateAccount(
                "550e8400-e29b-41d4-a716-446655440002",
                FtmsAccountType.SAVINGS,
                "USD"
        );

        // Act
        FtmsAccount entity = FtmsAccountMapper.toFtmsAccount(create);

        // Assert
        assertThat(entity.getCustomerId()).isEqualTo("550e8400-e29b-41d4-a716-446655440002");
        assertThat(entity.getAccountType()).isEqualTo(FtmsAccountType.SAVINGS);
        assertThat(entity.getCurrency()).isEqualTo("USD");

        // And: mapper should not set fields it doesn't know yet (typically null)
        assertThat(entity.getUuid()).isNull();
        assertThat(entity.getBalance()).isNull();     // or zero if your builder defaults it
        assertThat(entity.getStatus()).isNull();
        assertThat(entity.getCreatedAt()).isNull();
        assertThat(entity.getUpdatedAt()).isNull();
    }
}
