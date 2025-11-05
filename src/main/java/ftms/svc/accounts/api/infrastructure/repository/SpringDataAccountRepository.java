package ftms.svc.accounts.api.infrastructure.repository;

import ftms.svc.accounts.api.domain.model.FtmsAccount;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataAccountRepository extends JpaRepository<FtmsAccount, String> {
}
