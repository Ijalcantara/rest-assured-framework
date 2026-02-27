package tests.resiliency;

import clients.CloudflareClient;
import core.BaseApiTest;
import io.qameta.allure.*;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@Epic("Cloudflare API")
@Feature("Protocol Validation")
@DisplayName("CloudflareProtocolTests")
public class CloudflareProtocolTests extends BaseApiTest {

    @Test
    @Tag("negative")
    @Story("Unauthenticated requests should fail")
    @Description("Verify Cloudflare API rejects calls without authentication")
    void should_fail_when_calling_cloudflare_without_auth() {

        CloudflareClient client = new CloudflareClient();

        Response res = client.callUsingHttp(); // uses your https://api.cloudflare.com baseUrl

        Allure.attachment("Response Body", res.asString());
        Allure.attachment("Status Code", String.valueOf(res.statusCode()));

        int status = res.statusCode();

        // Cloudflare should not return 2xx without auth
        assertTrue(status >= 400 && status < 500,
                "Expected 4xx rejection without auth, but got: " + status);

        // Optional: If Cloudflare returns JSON with success/errors, validate basic contract
        // (Don't hardcode exact message unless stable.)
        Boolean success = res.jsonPath().getBoolean("success");
        if (success != null) {
            assertFalse(success, "Expected success=false for unauthenticated request");
        }
    }
}