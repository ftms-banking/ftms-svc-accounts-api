package ftms.svc.accounts.api.application.commands;

import ftms.svc.accounts.api.infrastructure.constants.FtmsAccountType;

public record CreateAccountCmd(String customerId,
                               String accountNumber,
                               FtmsAccountType type,
                               String currency) {}
