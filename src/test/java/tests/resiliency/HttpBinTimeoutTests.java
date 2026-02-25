package tests.resiliency;

import core.BaseApiTest;
import core.RequestSpecFactory;
import io.qameta.allure.*;
import io.restassured.RestAssured;
import io.restassured.config.HttpClientConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import utils.reusablemethod.ReusableMethod;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertThrows;

@Epic("HttpBin API")
@Feature("Timeout / Resiliency Tests")
@DisplayName("HttpBinTimeoutTests")
public class HttpBinTimeoutTests extends BaseApiTest {

    @Test
    @Tag("#TC01")
    @Story("Delay endpoint should timeout")
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

            ReusableMethod.attachApiCall(null, res);
            ReusableMethod.validateRequestSection(Map.of());
            ReusableMethod.validateStatusSection(res, res.statusCode());
            ReusableMethod.validateResponseSection(res);
        });
    }
}