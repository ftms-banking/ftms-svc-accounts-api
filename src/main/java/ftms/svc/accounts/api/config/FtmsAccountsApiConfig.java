package ftms.svc.accounts.api.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
public class FtmsAccountsApiConfig {
    @Value("{api.version}")
    private String apiVersion;

    @Value("${downstream.ftms.api.customers.url}")
    private String customerApiUrl;

    @Value("${downstream.ftms.api.customers.timeout}")
    private int customerApiTimeout;
}
