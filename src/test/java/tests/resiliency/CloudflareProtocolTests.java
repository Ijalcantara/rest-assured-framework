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

import static org.junit.jupiter.api.Assertions.assertTrue;

@Epic("Cloudflare API")
@Feature("Protocol Validation")
@DisplayName("CloudflareProtocolTests")
public class CloudflareProtocolTests extends BaseApiTest {

    private static final Logger log = LoggerFactory.getLogger(CloudflareProtocolTests.class);

    @Test
    @Tag("negative")
    @Story("Fail when using HTTP instead of HTTPS")
    @Description("Test verifies that making an HTTP request instead of HTTPS fails or redirects as expected")
    void Test17_should_fail_when_using_http_instead_of_https() {

        String testName = "Test17 - HTTP instead of HTTPS";

        Allure.step("Start test: " + testName);
        ReusableMethod.logTestStart(testName);

        CloudflareClient client = new CloudflareClient();

        try {
            // Step 1: Call API using HTTP (void step)
            Response res = client.callUsingHttpWithoutRedirect();

            // Step 2: Log & attach response manually
            ReusableMethod.logResponse(res);
            Allure.attachment("Response Body", res.asString());
            Allure.attachment("Status Code", String.valueOf(res.statusCode()));

            // Step 3: Validate non-200 response
            Allure.step("Validate that HTTP request fails or redirects", () ->
                    assertTrue(res.statusCode() != 200, "Expected HTTP request to fail or redirect"));

        } catch (Exception e) {
            log.info("Connection failed as expected: {}", e.getMessage());
            Allure.step("Expected exception occurred on HTTP request: " + e.getMessage());
            assertTrue(true, "Expected exception on HTTP request");
        }

        ReusableMethod.logTestEnd(testName);
        Allure.step("End test: " + testName);
    }
}