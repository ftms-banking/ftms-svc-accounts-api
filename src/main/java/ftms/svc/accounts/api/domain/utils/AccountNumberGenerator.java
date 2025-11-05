package ftms.svc.accounts.api.domain.utils;

import lombok.extern.slf4j.Slf4j;

import java.time.Instant;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
public class AccountNumberGenerator {

    private static final AtomicLong COUNTER = new AtomicLong(0);

    public static String createAccountNumber() {
        long epochPart = Instant.now().toEpochMilli(); // 13 digits
        long counterPart = COUNTER.getAndIncrement() % 10000; // 4 digits
        int randomPart = ThreadLocalRandom.current().nextInt(100, 999); // 3 digits

        // Build complete number
        String generatedAccountNumber = String.format("%013d%04d%03d", epochPart, counterPart, randomPart);

        // Just in case, enforce exact 20 chars (padding or truncating)
        if (generatedAccountNumber.length() > 20) {
            generatedAccountNumber = generatedAccountNumber.substring(generatedAccountNumber.length() - 20); // take last 20 for uniqueness
        } else if (generatedAccountNumber.length() < 20) {
            generatedAccountNumber = String.format("%020d", Long.parseLong(generatedAccountNumber)); // pad with leading zeros
        }
        log.debug("[createAccountNumber] Generated account number: {}", generatedAccountNumber);
        return generatedAccountNumber;
    }
}

