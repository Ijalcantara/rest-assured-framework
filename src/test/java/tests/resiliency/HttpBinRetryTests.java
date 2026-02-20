package tests.resiliency;

import core.BaseApiTest;
import core.RequestSpecFactory;
import io.qameta.allure.*;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.RetryUtil;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Epic("HttpBin API")
@Feature("Retry / Resiliency Tests")
public class HttpBinRetryTests extends BaseApiTest {

    private static final Logger log = LoggerFactory.getLogger(HttpBinRetryTests.class);

    @Test
    @Tag("resiliency")
    @Story("Retry GET /get until success")
    @Description("Test demonstrates retry logic with /get endpoint until a 200 OK response is received")
    void Test19_retry_demo_should_eventually_get_200() {

        log.info("========== START Test19 - Retry Logic ==========");
        log.info("Calling /get with retry up to 5 attempts");

        // Execute GET with retry logic
        Response res = RetryUtil.until(
                () -> io.restassured.RestAssured.given()
                        .spec(RequestSpecFactory.httpBin())
                        .when()
                        .get("/get"),
                r -> r.statusCode() == 200,
                5,
                Duration.ofSeconds(1)
        );

        log.info("Final Status after retry: {}", res.statusCode());

        // Validate final status
        assertEquals(HttpStatus.SC_OK, res.statusCode(), "Expected 200 OK");

        log.info("========== END Test19 ==========");
    }
}