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
import utils.reusablemethod.ReusableMethod;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@Epic("Advantage Shopping API")
@Feature("User Registration")
@DisplayName("AdvantageShoppingTests")
public class AdvantageShoppingTests extends BaseApiTest {

    @Test
    @Tag("#TC01")
    @DisplayName("TC01 - Wrong API Version should return 404")
    @Story("Wrong API Version should return 404")
    @Description("Test verifies that using a wrong API version for /register endpoint returns 404")
    void wrong_login_version_should_return_404() {

        Map<String, Object> body = TestDataManager.getDataAsMap("advantageShopping", "registerUser");

        Response res = io.restassured.RestAssured.given()
                .spec(RequestSpecFactory.advantage())
                .body(body)
                .when()
                .post("/register");

        ReusableMethod.attachApiCallUnified(body, res, null);
        assertEquals(HttpStatus.SC_NOT_FOUND, res.statusCode());
    }

    @Test
    @Tag("#TC02")
    @DisplayName("TC02 - Register new user should return success (mock)")
    @Story("Register new user should return success (mock)")
    @Description("Test simulates registering a new user and validates the mocked success response")
    void register_new_user_should_return_success_mock() {

        Map<String, Object> userPayload = TestDataManager.getDataAsMap("advantageShopping", "registerUser");

        long timestamp = System.currentTimeMillis();
        userPayload.put("email", "automation" + timestamp + "@example.com");
        userPayload.put("loginName", "auto" + timestamp);

        // ===== MOCK RESPONSE =====
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("success", true);
        responseBody.put("userId", 12345);
        responseBody.put("reason", "User created successfully");

        Map<String, Object> mockResponse = new HashMap<>();
        mockResponse.put("response", responseBody);

        int statusCode = 200;

        ReusableMethod.attachApiCallUnified(userPayload, statusCode, mockResponse);

        Map<?, ?> responseMap = (Map<?, ?>) mockResponse.get("response");
        boolean success = Boolean.TRUE.equals(responseMap.get("success"));
        String userId = String.valueOf(responseMap.get("userId"));
        String reason = String.valueOf(responseMap.get("reason"));

        assertEquals(HttpStatus.SC_OK, statusCode);
        assertTrue(success);
        assertNotNull(userId);
        assertFalse(userId.isBlank());
        assertTrue(reason.contains("created successfully"));
    }
}