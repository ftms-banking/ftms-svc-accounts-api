package ftms.svc.accounts.api.config;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import static org.assertj.core.api.Assertions.assertThat;

class FtmsAccountsApiConfigRunnerTest {

    private final ApplicationContextRunner contextRunner =
            new ApplicationContextRunner()
                    .withUserConfiguration(FtmsAccountsApiConfig.class)  // load only our config class
                    .withPropertyValues(                                 // fake application.yml values
                            "api.version=v1",
                            "downstream.ftms.api.customers.url=http://localhost:8080/api/v1/customers/{customerId}",
                            "downstream.ftms.api.customers.timeout=15"
                    );

    @Test
    void shouldBindPropertiesCorrectly() {
        contextRunner.run(context -> {

            FtmsAccountsApiConfig config = context.getBean(FtmsAccountsApiConfig.class);

            assertThat(config.getApiVersion()).isEqualTo("v1");
            assertThat(config.getCustomerApiUrl())
                    .isEqualTo("http://localhost:8080/api/v1/customers/{customerId}");
            assertThat(config.getCustomerApiTimeout()).isEqualTo(15);
        });
    }
}

