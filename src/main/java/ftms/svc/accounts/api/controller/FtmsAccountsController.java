package ftms.svc.accounts.api.controller;

import ftms.svc.accounts.api.constants.FtmsAccountsApiConstants;
import ftms.svc.accounts.api.domain.FtmsAccountResult;
import ftms.svc.accounts.api.domain.FtmsCreateAccount;
import ftms.svc.accounts.api.domain.service.FtmsAccountService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Map;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(FtmsAccountsApiConstants.FTMS_ACCOUNTS_API_V1)
public class FtmsAccountsController {

    private final FtmsAccountService ftmsAccountService;

    @PostMapping
    public ResponseEntity<FtmsAccountResult> createFtmsAccount(
            @Valid @RequestBody FtmsCreateAccount request,
            HttpServletRequest httpServletRequest) {

        return ResponseEntity
                .created(
                        URI.create(httpServletRequest.getRequestURI())
                ).body(ftmsAccountService.createFtmsAccount(request));
    }



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

}
