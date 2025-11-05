package ftms.svc.accounts.api.application.commands;

import ftms.svc.accounts.api.infrastructure.constants.FtmsAccountStatus;

import java.util.Optional;

public record PatchAccountCmd(Optional<FtmsAccountStatus> status, Optional<String> currency) {}
