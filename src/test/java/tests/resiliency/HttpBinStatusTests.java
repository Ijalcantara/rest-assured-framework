package tests.resiliency;

import clients.HttpBinClient;
import core.BaseApiTest;
import io.qameta.allure.*;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import utils.reusablemethod.ReusableMethod;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Epic("HttpBin API")
@Feature("Status & Resiliency Tests")
@DisplayName("HttpBinStatusTests")
public class HttpBinStatusTests extends BaseApiTest {

    private final HttpBinClient api = new HttpBinClient();

    @Test
    @Tag("#TC01")
    @Story("Return HTTP 500")
    @DisplayName("TC01 - Return HTTP 500")
    void Test6_should_return_500() {
        Response res = api.status(HttpStatus.SC_INTERNAL_SERVER_ERROR);
        ReusableMethod.attachApiCall(null, res);

        ReusableMethod.validateRequestSection(Map.of());
        ReusableMethod.validateStatusSection(res, HttpStatus.SC_INTERNAL_SERVER_ERROR);
        ReusableMethod.validateResponseSection(res);

        assertEquals(HttpStatus.SC_INTERNAL_SERVER_ERROR, res.statusCode());
    }

    @Test
    @Tag("#TC02")
    @Story("Return HTTP 429")
    @DisplayName("TC02 - Return HTTP 429")
    void Test20_should_return_429() {
        Response res = api.status(429);
        ReusableMethod.attachApiCall(null, res);

        ReusableMethod.validateRequestSection(Map.of());
        ReusableMethod.validateStatusSection(res, 429);
        ReusableMethod.validateResponseSection(res);

        assertEquals(429, res.statusCode());
    }

    @Test
    @Tag("#TC03")
    @Story("Retry until 200 OK")
    @DisplayName("TC03 - Retry until 200 OK")
    void Test19_should_retry_until_200_OK() {
        Response res = api.status(HttpStatus.SC_INTERNAL_SERVER_ERROR);

        int maxRetries = 5;
        for (int attempt = 1; res.statusCode() != 200 && attempt <= maxRetries; attempt++) {
            res = api.getCall();
        }

        ReusableMethod.attachApiCall(null, res);
        ReusableMethod.validateRequestSection(Map.of());
        ReusableMethod.validateStatusSection(res, 200);
        ReusableMethod.validateResponseSection(res);

        assertEquals(200, res.statusCode());
    }

    @Test
    @Tag("#TC04")
    @Story("Request timeout")
    @DisplayName("TC04 - Request timeout")
    void Test18_should_timeout_when_delay_exceeds_timeout() {
        assertThrows(Exception.class, () -> api.delayWithTimeout(10, 5000));
    }
}