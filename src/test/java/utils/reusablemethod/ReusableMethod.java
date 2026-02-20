package utils.reusablemethod;

import clients.DummyJsonClient;
import io.qameta.allure.Allure;
import io.restassured.response.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.LogSanitizerUtil;
import utils.SensitiveDataAssertsUtil;

import java.nio.charset.StandardCharsets;

public final class ReusableMethod {

    private ReusableMethod() {}

    private static final Logger log = LoggerFactory.getLogger(ReusableMethod.class);
    public static final DummyJsonClient api = new DummyJsonClient();
    // -----------------------------
    // Logging
    // -----------------------------
    public static void logResponse(Response res) {
        // Existing logging
        log.info("Received Status: {}", res.statusCode());
        try {
            String msg = res.jsonPath().getString("message");
            if (msg != null) log.info("Response Message: {}", LogSanitizerUtil.maskSensitive(msg));
        } catch (Exception e) {
            log.info("No message in response body.");
        }
        log.info("Full Response: {}", LogSanitizerUtil.maskSensitive(res.asString()));

        // ✅ Attach response to Allure
        Allure.addAttachment("API Response", "application/json",
                LogSanitizerUtil.maskSensitive(res.asString()),
                StandardCharsets.UTF_8.name());
    }

    public static void logRequest(String name, Object requestPayload) {
        // Attach request payload to Allure
        Allure.addAttachment(name, "application/json",
                LogSanitizerUtil.maskSensitiveObject(requestPayload),
                StandardCharsets.UTF_8.name());

        // Console log
        log.info("{}: {}", name, LogSanitizerUtil.maskSensitiveObject(requestPayload));
    }

    public static void logTestStart(String testName) {
        log.info("========== START {} ==========", testName);
    }

    public static void logTestEnd(String testName) {
        log.info("========== END {} ==========", testName);
    }

    // -----------------------------
    // Reusable Assertions
    // -----------------------------
    public static void assertLoginResponse(Response res, int expectedStatus, boolean checkToken) {
        org.junit.jupiter.api.Assertions.assertEquals(expectedStatus, res.statusCode());
        SensitiveDataAssertsUtil.assertDoesNotContainPassword(res.asString());
        if (checkToken) {
            org.junit.jupiter.api.Assertions.assertNotNull(res.jsonPath().getString("accessToken"));
        }
    }
}
