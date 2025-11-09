package ftms.svc.accounts.api.domain.mapper;

import ftms.svc.accounts.api.domain.FtmsAccountResult;
import ftms.svc.accounts.api.domain.FtmsCreateAccount;
import ftms.svc.accounts.api.domain.entity.FtmsAccount;

public class FtmsAccountMapper {

    private FtmsAccountMapper() {}

    public static FtmsAccountResult toFtmsAccountResult(FtmsAccount ftmsAccount) {
        return FtmsAccountResult.builder()
                .uuid(ftmsAccount.getUuid())
                .accountType(ftmsAccount.getAccountType())
                .customerId(ftmsAccount.getCustomerId())
                .currency(ftmsAccount.getCurrency())
                .balance(ftmsAccount.getBalance())
                .status(ftmsAccount.getStatus())
                .createdAt(ftmsAccount.getCreatedAt())
                .updatedAt(ftmsAccount.getUpdatedAt())
                .build();
    }

    public static FtmsAccount toFtmsAccount(FtmsCreateAccount ftmsCreateAccount) {
        return FtmsAccount.builder()
                .customerId(ftmsCreateAccount.getCustomerId())
                .accountType(ftmsCreateAccount.getAccountType())
                .currency(ftmsCreateAccount.getCurrency())
                .build();
    }
}
