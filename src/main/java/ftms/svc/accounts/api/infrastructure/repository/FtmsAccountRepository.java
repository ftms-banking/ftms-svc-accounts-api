package ftms.svc.accounts.api.infrastructure.repository;

import ftms.svc.accounts.api.domain.entity.FtmsAccount;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FtmsAccountRepository extends JpaRepository<FtmsAccount, String> {
}
