package ftms.svc.accounts.api.interfaces.dto.responce;

import java.math.BigDecimal;

public record AccountResponce(
        String accountId,
        String accountNumber,
        String status,
        BigDecimal balance
) {
}
