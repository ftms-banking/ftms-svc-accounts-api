package ftms.svc.accounts.api.domain.repository;

import ftms.svc.accounts.api.domain.model.FtmsAccount;

import java.util.Optional;


public interface AccountRepository {
    FtmsAccount save(FtmsAccount account);
    Optional<FtmsAccount> findById(String id);
}
