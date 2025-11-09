package ftms.svc.accounts.api;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class FtmsSvcAccountsApplicationTest {

    @Test
    void contextLoad(){
        Assertions.assertTrue(true);
    }
}
