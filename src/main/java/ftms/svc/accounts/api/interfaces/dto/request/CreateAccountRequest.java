package ftms.svc.accounts.api.interfaces.dto.request;

import ftms.svc.accounts.api.infrastructure.constants.FtmsAccountType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;


public record CreateAccountRequest(
        String customerId,
        String accountNumber,
        FtmsAccountType accountType,
        String currency
) {}
