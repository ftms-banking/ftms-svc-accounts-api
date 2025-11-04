package ftms.svc.accounts.api.controller;

import ftms.svc.accounts.api.constants.FtmsAccountsApiConstants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(FtmsAccountsApiConstants.FTMS_ACCOUNTS_API_V1)
public class FtmsAccountsController {

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
