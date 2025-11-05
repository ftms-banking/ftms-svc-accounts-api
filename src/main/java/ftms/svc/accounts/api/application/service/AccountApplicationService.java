package ftms.svc.accounts.api.application.service;

import ftms.svc.accounts.api.application.commands.CreateAccountCmd;
import ftms.svc.accounts.api.domain.model.FtmsAccount;
import ftms.svc.accounts.api.domain.repository.AccountRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AccountApplicationService {
        private final AccountRepository repository;

        @Transactional
        public FtmsAccount create(CreateAccountCmd cmd){
                FtmsAccount account = FtmsAccount.builder()
                        .customerId(cmd.customerId())
                        .accountType(cmd.type())
                        .accountNumber(cmd.accountNumber())
                        .currency(cmd.currency()).build();
                return repository.save(account);
        }

}
