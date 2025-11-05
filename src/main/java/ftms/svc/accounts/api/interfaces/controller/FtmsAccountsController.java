package ftms.svc.accounts.api.interfaces.controller;

import ftms.svc.accounts.api.application.commands.CreateAccountCmd;
import ftms.svc.accounts.api.application.service.AccountApplicationService;
import ftms.svc.accounts.api.domain.model.FtmsAccount;
import ftms.svc.accounts.api.infrastructure.constants.FtmsAccountType;
import ftms.svc.accounts.api.infrastructure.constants.FtmsAccountsApiConstants;
import ftms.svc.accounts.api.interfaces.dto.request.CreateAccountRequest;
import ftms.svc.accounts.api.interfaces.dto.responce.AccountResponce;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
//@RequestMapping(FtmsAccountsApiConstants.FTMS_ACCOUNTS_API_V1)
@RequestMapping("/api/v1/accounts")
public class FtmsAccountsController {

    private final AccountApplicationService service;

    /**
     * Returns basic health information of the service.
     *
     * @return a map containing status, service name, and version
     */
    @GetMapping("/health")
    public Map<String, String> health() {
        return Map.of(
                "status", "UP",
                "service", "FTMS Accounts Service",
                "version", "v1"
        );
    }

    @PostMapping
    public ResponseEntity<AccountResponce> create(@RequestBody CreateAccountRequest request){

        CreateAccountCmd cmd = new CreateAccountCmd(
                request.customerId(),
                generateAccountNumber(),
                request.accountType(),
                request.currency()
        );

        FtmsAccount saved = service.create(cmd);

        return ResponseEntity.ok(new AccountResponce(
                saved.getUuid(),
                saved.getAccountNumber(),
                saved.getStatus().name(),
                saved.getBalance()
        ));

    }

    private String generateAccountNumber() {
        return "ACC" + System.currentTimeMillis(); // example
    }


}
