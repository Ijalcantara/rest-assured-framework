package tests.resiliency;

import core.BaseApiTest;
import core.RequestSpecFactory;
import io.qameta.allure.*;
import io.restassured.RestAssured;
import io.restassured.config.HttpClientConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.response.Response;
import org.junit.jupiter.api.*;
import utils.ApiAllureUtil;

import static org.junit.jupiter.api.Assertions.assertThrows;

@Epic("HttpBin API")
@Feature("Timeout / Resiliency Tests")
@DisplayName("HttpBinTimeoutTests")
public class HttpBinTimeoutTests extends BaseApiTest {

    @Test
    @Tag("#TC01")
    @DisplayName("TC01 - /delay/10 with 5s timeout")
    void Test18_delay_should_timeout_with_5_seconds() {

        RestAssuredConfig config = RestAssured.config()
                .httpClient(HttpClientConfig.httpClientConfig()
                        .setParam("http.connection.timeout", 5000)
                        .setParam("http.socket.timeout", 5000));

        assertThrows(Exception.class, () -> {
            Response res = RestAssured.given()
                    .spec(RequestSpecFactory.httpBin())
                    .config(config)
                    .when()
                    .get("/delay/10")
                    .then()
                    .extract()
                    .response();

            ApiAllureUtil.validateApiScenario(
                    "Call /delay/10 endpoint with a 5-second timeout configured.",
                    null,
                    res,
                    200
            );
            ApiAllureUtil.attachApiCall(null, res);
        });
    }
}