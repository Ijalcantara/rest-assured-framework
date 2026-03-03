package tests.resiliency;

import clients.CloudflareClient;
import core.BaseApiTest;
import io.qameta.allure.*;
import io.restassured.response.Response;
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.ApiAllureUtil;

import static org.junit.jupiter.api.Assertions.assertTrue;

@Epic("Cloudflare API")
@Feature("Protocol Validation")
@DisplayName("CloudflareProtocolTests")
public class CloudflareProtocolTests extends BaseApiTest {

    private static final Logger log = LoggerFactory.getLogger(CloudflareProtocolTests.class);

    @Test
    @Tag("negative")
    @DisplayName("TC17 - HTTP request should fail instead of HTTPS")
    void test_should_fail_when_using_http_instead_of_https() {

        ApiAllureUtil.logScenario("Attempt HTTP request to Cloudflare endpoint (should fail or redirect).");

        CloudflareClient client = new CloudflareClient();
        Response response = client.callUsingHttpWithoutRedirect();

        // Attach response only (no request payload)
        ApiAllureUtil.attachApiCall(null, response);

        // Validate expected status: >=400 or redirect
        boolean validStatus = response.getStatusCode() >= 400
                || response.getStatusCode() == 301
                || response.getStatusCode() == 302;

        ApiAllureUtil.validateResponseBody(response);
        assertTrue(validStatus, "Expected HTTP request to fail or redirect, but got status code: " + response.getStatusCode());

        log.info("TC17 validation PASSED with status code: {}", response.getStatusCode());
    }
}