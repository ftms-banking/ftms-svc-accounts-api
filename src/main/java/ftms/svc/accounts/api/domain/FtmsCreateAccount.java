package ftms.svc.accounts.api.domain;

import ftms.svc.accounts.api.constants.FtmsAccountType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor
public class FtmsCreateAccount {
        @NotBlank
        @Size(min = 36, max = 36)
        private String customerId;

        @NotNull(message = "Account type must not be null")
        FtmsAccountType accountType;

        @NotBlank
        @Size(min = 3, max = 3)
        @Pattern(regexp = "^[A-Z]{3}$")
        private String currency;
}
