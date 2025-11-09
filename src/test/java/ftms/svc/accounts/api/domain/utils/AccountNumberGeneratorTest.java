package ftms.svc.accounts.api.domain.utils;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AccountNumberGeneratorTest {

    @Test
    void generatedAccountNumberShouldHaveLength20() {
        String accountNumber = AccountNumberGenerator.createAccountNumber();

        assertThat(accountNumber).hasSize(20);
    }

    @Test
    void generatedAccountNumberShouldBeNumeric() {
        String accountNumber = AccountNumberGenerator.createAccountNumber();

        assertThat(accountNumber).matches("\\d{20}"); // exactly 20 digits
    }

    @Test
    void generatedAccountNumbersShouldBeUnique() {
        String first = AccountNumberGenerator.createAccountNumber();
        String second = AccountNumberGenerator.createAccountNumber();

        assertThat(first).isNotEqualTo(second); // counter ensures uniqueness
    }

    @Test
    void generatedAccountNumberShouldIncreaseWithCounter() {
        String acc1 = AccountNumberGenerator.createAccountNumber();
        String acc2 = AccountNumberGenerator.createAccountNumber();

        // Take the counter digits from last 7 digits (4 for counter, 3 for random)
        long counter1 = Long.parseLong(acc1.substring(13, 17));
        long counter2 = Long.parseLong(acc2.substring(13, 17));

        assertThat(counter2).isEqualTo(counter1 + 1);
    }
}
