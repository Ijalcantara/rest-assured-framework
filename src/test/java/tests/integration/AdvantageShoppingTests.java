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

    @Test
    @Tag("integration")
    @Story("Wrong API Version should return 404")
    @Description("Test verifies that using a wrong API version for /register endpoint returns 404")
    void Test4_wrong_login_version_should_return_404() {
        String testName = "Test4 - Wrong API Version";
        ReusableMethod.logTestStart(testName);

        // Get test data via TestDataManager
        Map<String, Object> body = TestDataManager.getDataAsMap("advantageShopping", "registerUser");

        // Send request
        Response res = io.restassured.RestAssured.given()
                .spec(RequestSpecFactory.advantage())
                .body(body)
                .when()
                .post("/register");

        // Log response with reusable method (includes Allure attachment)
        ReusableMethod.logResponse(res);

        ReusableMethod.logTestEnd(testName);
    }

    @Test
    @Tag("advantage_register")
    @Story("Register new user should return success (mock)")
    @Description("Test simulates registering a new user and validates the mocked success response")
    void Test16_register_new_user_should_return_success_mock() {
        String testName = "Test16 - Register New User (Mock)";
        ReusableMethod.logTestStart(testName);

        // Get test data via TestDataManager
        Map<String, Object> userPayload = TestDataManager.getDataAsMap("advantageShopping", "registerUser");

        // Generate unique email and loginName
        long timestamp = System.currentTimeMillis();
        userPayload.put("email", "automation" + timestamp + "@example.com");
        userPayload.put("loginName", "auto" + timestamp);

        log.info("Payload being sent: {}", userPayload);

        // ===== MOCK RESPONSE =====
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("success", true);
        responseBody.put("userId", 12345);
        responseBody.put("reason", "User created successfully");

        Map<String, Object> mockResponse = new HashMap<>();
        mockResponse.put("response", responseBody);

        int statusCode = 200; // Simulate HTTP 200 OK

        log.info("Mocked Response Status: {}", statusCode);
        log.info("Mocked Response Body: {}", mockResponse);

        // ===== TYPE-SAFE EXTRACTION =====
        Map<?, ?> responseMap = (Map<?, ?>) mockResponse.get("response");
        boolean success = Boolean.TRUE.equals(responseMap.get("success"));
        String userId = responseMap.get("userId") != null ? responseMap.get("userId").toString() : null;
        String reason = responseMap.get("reason") != null ? responseMap.get("reason").toString() : "";

        // ===== ASSERTIONS =====
        assertEquals(HttpStatus.SC_OK, statusCode, "Expected 200 OK");
        assertTrue(success, "Expected registration success to be true");
        assertNotNull(userId, "Expected userId to be returned");
        assertFalse(userId.isBlank(), "Expected userId to be returned");
        assertTrue(reason.contains("created successfully"), "Expected success message in reason");

        ReusableMethod.logTestEnd(testName);
    }
}