package tests.resiliency;

import core.BaseApiTest;
import core.RequestSpecFactory;
import io.qameta.allure.*;
import io.restassured.RestAssured;
import io.restassured.config.RestAssuredConfig;
import io.restassured.config.HttpClientConfig;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.reusablemethod.ReusableMethod;

import static org.junit.jupiter.api.Assertions.assertThrows;

@Epic("HttpBin API")
@Feature("Timeout / Resiliency Tests")
@DisplayName("HttpBinTimeoutTests")
public class HttpBinTimeoutTests extends BaseApiTest {

    private static final Logger log = LoggerFactory.getLogger(HttpBinTimeoutTests.class);

    @Test
    @Tag("resiliency")
    @Story("Delay endpoint should timeout")
    @Description("Test verifies that calling /delay/10 with a 5-second timeout throws an exception")
    void Test18_delay_should_timeout_with_5_seconds() {
        String testName = "Test18 - Timeout Validation";

        Allure.step("Start test: " + testName);
        ReusableMethod.logTestStart(testName);

        // Use modern RestAssuredConfig for timeouts
        RestAssuredConfig config = RestAssured.config()
                .httpClient(HttpClientConfig.httpClientConfig()
                        .setParam("http.connection.timeout", 5000)
                        .setParam("http.socket.timeout", 5000));

        Allure.step("Call /delay/10 with 5s timeout and expect exception", () -> {
            assertThrows(Exception.class, () -> {
                RestAssured.given()
                        .spec(RequestSpecFactory.httpBin())
                        .config(config)
                        .when()
                        .get("/delay/10")
                        .then()
                        .statusCode(HttpStatus.SC_OK);
            });
        });

        log.info("Timeout occurred as expected.");

        ReusableMethod.logTestEnd(testName);
        Allure.step("End test: " + testName);
    }
}