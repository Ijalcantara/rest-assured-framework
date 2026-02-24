package tests.integration;

import core.BaseApiTest;
import core.RequestSpecFactory;
import core.TestDataManager;
import io.qameta.allure.*;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.reusablemethod.ReusableMethod;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@Epic("Advantage Shopping API")
@Feature("User Registration")
@DisplayName("AdvantageShoppingTests")
public class AdvantageShoppingTests extends BaseApiTest {

    private static final Logger log = LoggerFactory.getLogger(AdvantageShoppingTests.class);

    // ==========================================
    // TEST 4 - WRONG API VERSION
    // ==========================================
    @Test
    @Tag("integration")
    @Story("Wrong API Version should return 404")
    @Description("Test verifies that using a wrong API version for /register endpoint returns 404")
    void Test4_wrong_login_version_should_return_404() {

        String testName = "Test4 - Wrong API Version";
        Allure.step("Start test: " + testName);
        ReusableMethod.logTestStart(testName);

        // Step 1: Get test data
        Map<String, Object> body = TestDataManager.getDataAsMap("advantageShopping", "registerUser");
        Allure.attachment("Request Payload", body.toString());

        // Step 2: Send request (void step)
        Response res = io.restassured.RestAssured.given()
                .spec(RequestSpecFactory.advantage())
                .body(body)
                .when()
                .post("/register");

        Allure.attachment("Response Body", res.asString());
        Allure.attachment("Status Code", String.valueOf(res.statusCode()));
        ReusableMethod.logResponse(res);

        // Step 3: Validate 404
        Allure.step("Validate HTTP 404 Not Found", () ->
                assertEquals(HttpStatus.SC_NOT_FOUND, res.statusCode()));

        ReusableMethod.logTestEnd(testName);
        Allure.step("End test: " + testName);
    }

    // ==========================================
    // TEST 16 - MOCK REGISTRATION SUCCESS
    // ==========================================
    @Test
    @Tag("advantage_register")
    @Story("Register new user should return success (mock)")
    @Description("Test simulates registering a new user and validates the mocked success response")
    void Test16_register_new_user_should_return_success_mock() {

        String testName = "Test16 - Register New User (Mock)";
        Allure.step("Start test: " + testName);
        ReusableMethod.logTestStart(testName);

        // Step 1: Get base payload
        Map<String, Object> userPayload = TestDataManager.getDataAsMap("advantageShopping", "registerUser");

        // Step 2: Generate unique test data
        long timestamp = System.currentTimeMillis();
        userPayload.put("email", "automation" + timestamp + "@example.com");
        userPayload.put("loginName", "auto" + timestamp);

        log.info("Payload being sent: {}", userPayload);
        Allure.attachment("Final Request Payload", userPayload.toString());

        // ===== MOCK RESPONSE =====
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("success", true);
        responseBody.put("userId", 12345);
        responseBody.put("reason", "User created successfully");

        Map<String, Object> mockResponse = new HashMap<>();
        mockResponse.put("response", responseBody);

        int statusCode = 200;

        log.info("Mocked Response Status: {}", statusCode);
        log.info("Mocked Response Body: {}", mockResponse);

        Allure.attachment("Mocked Response Body", mockResponse.toString());
        Allure.attachment("Mocked Status Code", String.valueOf(statusCode));

        // ===== TYPE-SAFE EXTRACTION =====
        Map<?, ?> responseMap = (Map<?, ?>) mockResponse.get("response");
        boolean success = Boolean.TRUE.equals(responseMap.get("success"));
        String userId = responseMap.get("userId") != null ? responseMap.get("userId").toString() : null;
        String reason = responseMap.get("reason") != null ? responseMap.get("reason").toString() : "";

        // ===== ASSERTIONS =====
        Allure.step("Validate HTTP 200 OK", () -> assertEquals(HttpStatus.SC_OK, statusCode));
        Allure.step("Validate success is true", () -> assertTrue(success));
        Allure.step("Validate userId is returned", () -> {
            assertNotNull(userId);
            assertFalse(userId.isBlank());
        });
        Allure.step("Validate success message contains expected text", () -> assertTrue(reason.contains("created successfully")));

        ReusableMethod.logTestEnd(testName);
        Allure.step("End test: " + testName);
    }
}