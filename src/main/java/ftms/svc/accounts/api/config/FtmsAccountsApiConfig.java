package ftms.svc.accounts.api.config;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@Slf4j
public class FtmsAccountsApiConfig {
    @Value("${api.version}")
    private String apiVersion;

    @Value("${downstream.ftms.api.customers.url}")
    private String customerApiUrl;

    @Value("${downstream.ftms.api.customers.timeout}")
    private int customerApiTimeout;


}
