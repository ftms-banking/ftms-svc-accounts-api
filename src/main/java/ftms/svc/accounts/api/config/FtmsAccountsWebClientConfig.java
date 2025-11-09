package ftms.svc.accounts.api.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ClientHttpConnector;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class FtmsAccountsWebClientConfig {

    private final FtmsAccountsApiConfig ftmsAccountsApiConfig;

    @Bean("ftmsCustomerApiWebClient")
    public WebClient ftmsCustomerApiWebClient() {
        HttpClient httpClient = HttpClient
                .create()
                .compress(true)
                .responseTimeout(
                        Duration.ofSeconds(ftmsAccountsApiConfig.getCustomerApiTimeout())
                );
        httpClient.warmup().block();
        ClientHttpConnector connector = new ReactorClientHttpConnector(httpClient);
        return WebClient.builder()
                .clientConnector(connector)
                .build();
    }

}
