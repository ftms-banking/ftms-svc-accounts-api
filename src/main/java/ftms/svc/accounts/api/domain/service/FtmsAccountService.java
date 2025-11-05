package ftms.svc.accounts.api.domain.service;

import ftms.svc.accounts.api.domain.FtmsAccountResult;
import ftms.svc.accounts.api.domain.FtmsCreateAccount;

public interface FtmsAccountService {
    FtmsAccountResult createFtmsAccount(FtmsCreateAccount ftmsCreateAccount);
}
