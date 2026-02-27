package tests.resiliency;

import clients.HttpBinClient;
import core.BaseApiTest;
import io.qameta.allure.*;
import io.restassured.response.Response;
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.RetryUtil;
import utils.ApiAllureUtil;

import java.time.Duration;

@Epic("HttpBin API")
@Feature("Retry / Resiliency Tests")
@DisplayName("HttpBinRetryTests")
public class HttpBinRetryTests extends BaseApiTest {

    private static final Logger log = LoggerFactory.getLogger(HttpBinRetryTests.class);
    private final HttpBinClient client = new HttpBinClient();

    @Test
    @Tag("resiliency")
    @Story("Retry GET /get until success")
    @DisplayName("TC01 - Retry /get until status 200")
    void retry_demo_should_eventually_get_200() {

        Response res = RetryUtil.until(
                client::getCall,
                r -> r.statusCode() == 200,
                5,
                Duration.ofSeconds(1)
        );

        ApiAllureUtil.validateApiScenario(
                "Retry GET /get endpoint until a successful status code (200) is returned.",
                null,
                res,
                200
        );
        ApiAllureUtil.attachApiCall(null, res);
    }
}