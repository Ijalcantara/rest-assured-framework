package tests.resiliency;

import clients.HttpBinClient;
import core.BaseApiTest;
import io.qameta.allure.*;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.*;
import utils.reusablemethod.ReusableMethod;

import static org.junit.jupiter.api.Assertions.*;

@Epic("HttpBin API")
@Feature("Status & Resiliency Tests")
@DisplayName("HttpBinStatusTests")
public class HttpBinStatusTests extends BaseApiTest {

    private final HttpBinClient api = new HttpBinClient();

    @Test
    @Tag("#TC01")
    @DisplayName("TC01 - Return HTTP 500")
    void Test6_should_return_500() {
        Response res = api.status(HttpStatus.SC_INTERNAL_SERVER_ERROR);

        ReusableMethod.validateApiScenario(
                "Call /status/500 endpoint.",
                null,
                res,
                HttpStatus.SC_INTERNAL_SERVER_ERROR
        );
        ReusableMethod.attachApiCall(null, res);
    }

    @Test
    @Tag("#TC02")
    @DisplayName("TC02 - Return HTTP 429")
    void Test20_should_return_429() {
        Response res = api.status(429);

        ReusableMethod.validateApiScenario(
                "Call /status/429 endpoint.",
                null,
                res,
                429
        );
        ReusableMethod.attachApiCall(null, res);
    }

    @Test
    @Tag("#TC03")
    @DisplayName("TC03 - Retry until 200 OK")
    void Test19_should_retry_until_200_OK() {
        Response res = api.status(HttpStatus.SC_INTERNAL_SERVER_ERROR);
        int maxRetries = 5;
        for (int attempt = 1; res.statusCode() != 200 && attempt <= maxRetries; attempt++) {
            res = api.getCall();
        }

        ReusableMethod.validateApiScenario(
                "Retry /get endpoint until HTTP 200 is returned.",
                null,
                res,
                200
        );
        ReusableMethod.attachApiCall(null, res);
    }

    @Test
    @Tag("#TC04")
    @DisplayName("TC04 - Request timeout")
    void Test18_should_timeout_when_delay_exceeds_timeout() {
        assertThrows(Exception.class, () -> api.delayWithTimeout(10, 5000));
    }
}