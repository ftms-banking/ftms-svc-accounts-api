package ftms.svc.accounts.api.domain.service.impl;

import ftms.svc.accounts.api.domain.FtmsAccountResult;
import ftms.svc.accounts.api.domain.FtmsCreateAccount;
import ftms.svc.accounts.api.domain.entity.FtmsAccount;
import ftms.svc.accounts.api.domain.mapper.FtmsAccountMapper;
import ftms.svc.accounts.api.domain.service.FtmsAccountService;
import ftms.svc.accounts.api.domain.utils.AccountNumberGenerator;
import ftms.svc.accounts.api.infrastructure.repository.FtmsAccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class FtmsAccountServiceImpl implements FtmsAccountService {

    private final FtmsAccountRepository ftmsAccountRepository;

    @Override
    public FtmsAccountResult createFtmsAccount(FtmsCreateAccount ftmsCreateAccount) {

        log.info("[createFtmsAccount] started creating ftms account for customer with id: {}", ftmsCreateAccount.getCustomerId());

        FtmsAccount ftmsAccount = FtmsAccountMapper.toFtmsAccount(ftmsCreateAccount);
        ftmsAccount.setAccountNumber(AccountNumberGenerator.createAccountNumber());
        FtmsAccount saved = ftmsAccountRepository.save(ftmsAccount);

        log.info("[createFtmsAccount] account created successfully for the customer with id: {}", saved.getCustomerId());

        return FtmsAccountMapper.toFtmsAccountResult(saved);
    }


}
