package ftms.svc.accounts.api.infrastructure.service.impl;

import ftms.svc.accounts.api.config.FtmsAccountsApiConfig;
import ftms.svc.accounts.api.infrastructure.service.FtmsCustomerApiService;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class FtmsCustomerApiServiceImplTest {

    private MockWebServer server;

    @BeforeEach
    void setUp() throws Exception {
        server = new MockWebServer();
        server.start(); // random free port
    }

    @AfterEach
    void tearDown() throws Exception {
        server.shutdown();
    }

    private FtmsCustomerApiService buildService() {
        // Point the config URL to the mock server, keeping your {customerId} template
        String urlTemplate = server.url("/api/v1/customers/{customerId}").toString();

        FtmsAccountsApiConfig cfg = new FtmsAccountsApiConfig();
        cfg.setCustomerApiUrl(urlTemplate);
        cfg.setCustomerApiTimeout(2); // seconds (not strictly needed here)

        // Simple WebClient is fine for unit tests
        WebClient client = WebClient.builder()
                .build();

        return new FtmsCustomerApiServiceImpl(client, cfg);
    }


    @Test
    void getCustomer_throws_on404() {
        // Arrange: downstream 404
        server.enqueue(new MockResponse()
                .setResponseCode(404)
                .setBody("Not Found")
                .setHeader("Content-Type", "text/plain; charset=utf-8"));

        FtmsCustomerApiService service = buildService();

        // Act + Assert: retrieve() maps 4xx/5xx to WebClientResponseException
        assertThatThrownBy(() -> service.getCustomer("missing-id"))
                .isInstanceOf(WebClientResponseException.NotFound.class);
    }

}
