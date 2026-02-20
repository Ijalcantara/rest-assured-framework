package utils;

import io.restassured.response.Response;

import java.time.Duration;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class RetryUtil {

    public static Response until(
            Supplier<Response> action,
            Predicate<Response> condition,
            int maxAttempts,
            Duration waitBetweenAttempts) {

        Response response = null;

        for (int attempt = 1; attempt <= maxAttempts; attempt++) {

            response = action.get();

            if (condition.test(response)) {
                return response;
            }

            try {
                Thread.sleep(waitBetweenAttempts.toMillis());
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("Retry interrupted", e);
            }
        }

        throw new RuntimeException("Condition not met after " + maxAttempts + " attempts");
    }
}
