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
    void Test6_should_return_500() {
        String testName = "Test6 - HTTP 500";
        Allure.step("Start test: " + testName);
        ReusableMethod.logTestStart(testName);

        Response res = api.status(HttpStatus.SC_INTERNAL_SERVER_ERROR);
        ReusableMethod.logResponse(res);

        // Attach once
        Allure.attachment("Response Body", res.asString());
        Allure.attachment("Status Code", String.valueOf(res.statusCode()));

        assertEquals(HttpStatus.SC_INTERNAL_SERVER_ERROR, res.statusCode());

        ReusableMethod.logTestEnd(testName);
        Allure.step("End test: " + testName);
    }

    @Test
    @Tag("resiliency")
    @Story("Return HTTP 429")
    void Test20_should_return_429() {
        String testName = "Test20 - HTTP 429";
        Allure.step("Start test: " + testName);
        ReusableMethod.logTestStart(testName);

        Response res = api.status(429);
        ReusableMethod.logResponse(res);

        Allure.attachment("Response Body", res.asString());
        Allure.attachment("Status Code", String.valueOf(res.statusCode()));

        assertEquals(429, res.statusCode());

        ReusableMethod.logTestEnd(testName);
        Allure.step("End test: " + testName);
    }

    @Test
    @Tag("resiliency")
    @Story("Retry until 200 OK")
    void Test19_should_retry_until_200_OK() {
        String testName = "Test19 - Retry Until 200";
        Allure.step("Start test: " + testName);
        ReusableMethod.logTestStart(testName);

        Response res = api.status(HttpStatus.SC_INTERNAL_SERVER_ERROR);
        log.info("Initial status: {}", res.statusCode());

        int maxRetries = 5;
        for (int attempt = 1; res.statusCode() != 200 && attempt <= maxRetries; attempt++) {
            res = api.getCall();
            log.info("Retry attempt {} status: {}", attempt, res.statusCode());
        }

        // Attach only the final response
        Allure.attachment("Final Response Body", res.asString());
        Allure.attachment("Final Status Code", String.valueOf(res.statusCode()));

        assertEquals(HttpStatus.SC_OK, res.statusCode());

        ReusableMethod.logTestEnd(testName);
        Allure.step("End test: " + testName);
    }

    @Test
    @Tag("resiliency")
    @Story("Request timeout")
    void Test18_should_timeout_when_delay_exceeds_timeout() {
        String testName = "Test18 - Timeout";
        Allure.step("Start test: " + testName);
        ReusableMethod.logTestStart(testName);

        assertThrows(Exception.class, () -> api.delayWithTimeout(10, 5000));
        log.info("Request timed out as expected.");

        ReusableMethod.logTestEnd(testName);
        Allure.step("End test: " + testName);
    }
}