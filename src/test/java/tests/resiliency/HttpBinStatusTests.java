package tests.resiliency;

import clients.HttpBinClient;
import core.BaseApiTest;
import io.qameta.allure.*;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.reusablemethod.ReusableMethod;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Epic("HttpBin API")
@Feature("Status & Resiliency Tests")
@DisplayName("HttpBinStatusTests")
public class HttpBinStatusTests extends BaseApiTest {

    private static final Logger log = LoggerFactory.getLogger(HttpBinStatusTests.class);
    private final HttpBinClient api = new HttpBinClient();

    @Test
    @Tag("resiliency")
    @Story("Return HTTP 500")
    @Description("Test verifies that calling /status/500 returns HTTP 500")
    void Test6_should_return_500() {
        String testName = "Test6 - HTTP 500";
        ReusableMethod.logTestStart(testName);

        Response res = api.status(HttpStatus.SC_INTERNAL_SERVER_ERROR);
        ReusableMethod.logResponse(res);

        assertEquals(HttpStatus.SC_INTERNAL_SERVER_ERROR, res.statusCode());

        ReusableMethod.logTestEnd(testName);
    }

    @Test
    @Tag("resiliency")
    @Story("Return HTTP 429")
    @Description("Test verifies that calling /status/429 returns HTTP 429")
    void Test20_should_return_429() {
        String testName = "Test20 - HTTP 429";
        ReusableMethod.logTestStart(testName);

        Response res = api.status(429);
        ReusableMethod.logResponse(res);

        assertEquals(429, res.statusCode());

        ReusableMethod.logTestEnd(testName);
    }

    @Test
    @Tag("resiliency")
    @Story("Retry until 200 OK")
    @Description("Test retries GET calls until a 200 OK response is returned, up to a maximum number of attempts")
    void Test19_should_retry_until_200_OK() {
        String testName = "Test19 - Retry Until 200";
        ReusableMethod.logTestStart(testName);

        int maxRetries = 5;
        int attempt = 0;

        Response res = api.status(HttpStatus.SC_INTERNAL_SERVER_ERROR);
        ReusableMethod.logResponse(res);

        while (res.statusCode() != 200 && attempt < maxRetries) {
            attempt++;
            log.info("Retry attempt {}", attempt);

            res = api.getCall();
            ReusableMethod.logResponse(res);
        }

        if (res.statusCode() != 200) {
            log.warn("Failed to get 200 OK after {} attempts, last status: {}", attempt, res.statusCode());
        }

        assertEquals(HttpStatus.SC_OK, res.statusCode());

        ReusableMethod.logTestEnd(testName);
    }

    @Test
    @Tag("resiliency")
    @Story("Request timeout")
    @Description("Test verifies that a delayed request exceeding the timeout throws an exception")
    void Test18_should_timeout_when_delay_exceeds_timeout() {
        String testName = "Test18 - Timeout";
        ReusableMethod.logTestStart(testName);

        assertThrows(Exception.class, () -> {
            api.delayWithTimeout(10, 5000);
        });

        log.info("Request timed out as expected.");

        ReusableMethod.logTestEnd(testName);
    }
}
