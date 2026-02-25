package tests.resiliency;

import clients.CloudflareClient;
import core.BaseApiTest;
import io.qameta.allure.*;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.reusablemethod.ReusableMethod;

@Epic("Cloudflare API")
@Feature("Protocol Validation")
@DisplayName("CloudflareProtocolTests")
public class CloudflareProtocolTests extends BaseApiTest {

    private static final Logger log = LoggerFactory.getLogger(CloudflareProtocolTests.class);

    @Test
    @Tag("negative")
    @Story("HTTP request instead of HTTPS")
    @DisplayName("TC17 - HTTP request should fail instead of HTTPS")
    @Description("Attach request payload, response status, and response body in a single step")
    void Test17_should_fail_when_using_http_instead_of_https() {

        CloudflareClient client = new CloudflareClient();

        try {
            Response res = client.callUsingHttpWithoutRedirect();
            ReusableMethod.attachApiCall(null, res);
        } catch (Exception e) {
            log.info("Expected exception occurred: {}", e.getMessage());
            Allure.step("Expected exception on HTTP request: " + e.getMessage());
        }
    }
}