package ftms.svc.accounts.api.config;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class FtmsAccountsWebClientConfigTest {

    private MockWebServer server;

    @BeforeEach
    void setUp() throws Exception {
        server = new MockWebServer();
        server.start(); // starts on a random free port
    }

    @AfterEach
    void tearDown() throws Exception {
        server.shutdown();
    }

    private WebClient buildClientWith1sTimeout() {
        FtmsAccountsApiConfig cfg = new FtmsAccountsApiConfig();
        cfg.setCustomerApiTimeout(1); // seconds

        FtmsAccountsWebClientConfig webCfg = new FtmsAccountsWebClientConfig(cfg);
        return webCfg.ftmsCustomerApiWebClient();
    }

    @Test
    void fastEndpointSucceeds() {
        server.enqueue(new MockResponse().setResponseCode(200).setBody("OK"));

        WebClient client = buildClientWith1sTimeout();

        // Act
        String body = client.get().uri(server.url("/fast").toString()).retrieve().bodyToMono(String.class).block(Duration.ofSeconds(2)); // well within timeout

        // Assert
        assertThat(body).isEqualTo("OK");
    }


    @Test
    void slowEndpointTimesOut() {
        server.enqueue(new MockResponse().setResponseCode(200).setBody("OK").setBodyDelay(1500, TimeUnit.MILLISECONDS)); // > 1s

        WebClient client = buildClientWith1sTimeout();


        org.assertj.core.api.ThrowableAssert.ThrowingCallable slowCall = () -> client.get().uri(server.url("/slow").toString()).retrieve().bodyToMono(String.class).block();

        assertThatThrownBy(slowCall).isInstanceOf(RuntimeException.class);
    }

}
