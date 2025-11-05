package ftms.svc.accounts.api.infrastructure.repository;

import ftms.svc.accounts.api.domain.model.FtmsAccount;
import ftms.svc.accounts.api.domain.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class JpaAccountRepository implements AccountRepository {

    private final SpringDataAccountRepository jpa;

    @Override
    public FtmsAccount save(FtmsAccount account) {
        return jpa.save(account);
    }

    @Override
    public Optional<FtmsAccount> findById(String id) {
        return Optional.empty();
    }
}
