package ftms.svc.accounts.api.infrastructure.service.impl;

import ftms.svc.accounts.api.config.FtmsAccountsApiConfig;
import ftms.svc.accounts.api.infrastructure.service.FtmsCustomerApiService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@RequiredArgsConstructor
@Service
public class FtmsCustomerApiServiceImpl implements FtmsCustomerApiService {

    @Qualifier("ftmsCustomerApiWebClient")
    private final WebClient ftmsCustomerApiWebClient;
    private final FtmsAccountsApiConfig ftmsAccountsApiConfig;

    @Override
    public Object getCustomer(String customerId) {
        return ftmsCustomerApiWebClient.get()
                .uri(ftmsAccountsApiConfig.getCustomerApiUrl(), customerId)
                .retrieve()
                .bodyToMono(Object.class)
                .block();
    }
}
