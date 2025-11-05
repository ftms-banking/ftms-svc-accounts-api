package ftms.svc.accounts.api.domain.service;

import ftms.svc.accounts.api.application.commands.CreateAccountCmd;
import ftms.svc.accounts.api.application.commands.PatchAccountCmd;
import ftms.svc.accounts.api.application.commands.UpdateAccountCmd;
import ftms.svc.accounts.api.domain.model.FtmsAccount;
import org.springframework.data.domain.Page;

public interface FtmsAccountService {

    FtmsAccount create(CreateAccountCmd cmd);
//

}
