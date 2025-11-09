package ftms.svc.accounts.api.domain;

import ftms.svc.accounts.api.constants.FtmsAccountType;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class FtmsAccountResult{
    private String uuid;
    private String customerId;
    private FtmsAccountType accountType;
    private String currency;
    private BigDecimal balance;
    private FtmsAccountStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
