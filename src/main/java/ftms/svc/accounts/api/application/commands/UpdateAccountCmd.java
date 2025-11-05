package ftms.svc.accounts.api.application.commands;

import ftms.svc.accounts.api.infrastructure.constants.FtmsAccountType;

public record UpdateAccountCmd(FtmsAccountType type, String currency) {}
