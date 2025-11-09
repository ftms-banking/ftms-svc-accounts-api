package ftms.svc.accounts.api.domain.service.impl;

import ftms.svc.accounts.api.constants.FtmsAccountType;
import ftms.svc.accounts.api.domain.FtmsAccountResult;
import ftms.svc.accounts.api.domain.FtmsAccountStatus;
import ftms.svc.accounts.api.domain.FtmsCreateAccount;
import ftms.svc.accounts.api.domain.entity.FtmsAccount;
import ftms.svc.accounts.api.infrastructure.exception.BusinessRuleValidationException;
import ftms.svc.accounts.api.infrastructure.repository.FtmsAccountRepository;
import ftms.svc.accounts.api.infrastructure.service.FtmsCustomerApiService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FtmsAccountServiceImplTest {

    @Mock
    FtmsAccountRepository ftmsAccountRepository;

    @Mock
    FtmsCustomerApiService ftmsCustomerApiService;

    @InjectMocks
    FtmsAccountServiceImpl service;

    @Test
    void createFtmsAccount_successfulFlow() {
        // Arrange
        FtmsCreateAccount req = new FtmsCreateAccount(
                "550e8400-e29b-41d4-a716-446655440002",
                FtmsAccountType.SAVINGS,
                "USD"
        );

        // Customer exists
        when(ftmsCustomerApiService.getCustomer(req.getCustomerId()))
                .thenReturn(new Object());

        // Capture what the service sends to repository.save(...)
        ArgumentCaptor<FtmsAccount> accountCaptor = ArgumentCaptor.forClass(FtmsAccount.class);

        // Stub repository to behave like DB: assign uuid, timestamps, defaults, etc.
        when(ftmsAccountRepository.save(any(FtmsAccount.class)))
                .thenAnswer(inv -> {
                    FtmsAccount a = inv.getArgument(0);
                    // Mimic DB/JPA side-effects
                    a.setUuid("generated-uuid-1234");
                    a.setStatus(FtmsAccountStatus.ACTIVE);
                    a.setBalance(new BigDecimal("0.00"));
                    a.setAvailableBalance(new BigDecimal("0.00"));
                    a.setOpenedAt(LocalDateTime.now());
                    a.setCreatedAt(LocalDateTime.now());
                    a.setUpdatedAt(LocalDateTime.now());
                    return a;
                });

        // Act
        FtmsAccountResult result = service.createFtmsAccount(req);

        // Assert: repository was called once
        verify(ftmsAccountRepository).save(accountCaptor.capture());
        FtmsAccount savedToRepo = accountCaptor.getValue();

        // The service should generate an account number before saving
        assertThat(savedToRepo.getAccountNumber()).isNotNull();
        assertThat(savedToRepo.getAccountNumber()).matches("\\d{20}"); // 20 digits

        // The fields from request should be propagated
        assertThat(savedToRepo.getCustomerId()).isEqualTo(req.getCustomerId());
        assertThat(savedToRepo.getAccountType()).isEqualTo(req.getAccountType());
        assertThat(savedToRepo.getCurrency()).isEqualTo(req.getCurrency());

        // The returned DTO should reflect what repository returned (via mapper)
        assertThat(result.getUuid()).isEqualTo("generated-uuid-1234");
        assertThat(result.getCustomerId()).isEqualTo(req.getCustomerId());
        assertThat(result.getAccountType()).isEqualTo(FtmsAccountType.SAVINGS);
        assertThat(result.getCurrency()).isEqualTo("USD");
        assertThat(result.getStatus()).isEqualTo(FtmsAccountStatus.ACTIVE);
        assertThat(result.getBalance()).isEqualByComparingTo("0.00");

        // And interactions:
        verify(ftmsCustomerApiService).getCustomer(req.getCustomerId());
        verifyNoMoreInteractions(ftmsCustomerApiService);
        verifyNoMoreInteractions(ftmsAccountRepository);
    }

    @Test
    void createFtmsAccount_throwsWhenCustomerNotFound() {
        FtmsCreateAccount req = new FtmsCreateAccount(
                "550e8400-e29b-41d4-a716-446655440099",
                FtmsAccountType.CHECKING,
                "USD"
        );

        when(ftmsCustomerApiService.getCustomer(req.getCustomerId()))
                .thenReturn(null); // simulate missing customer

        // Act + Assert
        assertThatThrownBy(() -> service.createFtmsAccount(req))
                .isInstanceOf(BusinessRuleValidationException.class)
                .hasMessageContaining("Customer not found");

        // Ensure repo was never called
        verifyNoInteractions(ftmsAccountRepository);
        verify(ftmsCustomerApiService).getCustomer(req.getCustomerId());
        verifyNoMoreInteractions(ftmsCustomerApiService);
    }
}
