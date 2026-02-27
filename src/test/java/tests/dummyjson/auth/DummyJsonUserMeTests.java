package tests.dummyjson.auth;

import clients.DummyJsonClient;
import constant.ApiPaths;
import constant.ConstantClass;
import core.BaseApiTest;
import core.RequestSpecFactory;
import core.TestDataManager;
import io.qameta.allure.*;
import io.restassured.response.Response;
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import utils.LoggerUtils;
import utils.reusablemethod.ReusableMethod;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Epic("DummyJson API")
@Feature("User Me Endpoint")
@DisplayName("DummyJsonUserMeTests")
public class DummyJsonUserMeTests extends BaseApiTest {

    private static final Logger log = LoggerUtils.getLogger(DummyJsonUserMeTests.class);
    private final DummyJsonClient api = new DummyJsonClient();

    // -------------------------------
    // Positive: valid token
    // -------------------------------
    @Story("Positive Scenarios")
    @Test
    @Tag("test8")
    @DisplayName("TC08 - Get user info with valid token")
    void user_me_valid_token_should_return_200() {

        // ===== Login to get fresh token =====
        Map<String, Object> loginPayload = TestDataManager.getDataAsMap(
                ConstantClass.DUMMYJSON,
                ConstantClass.LOGIN,
                ConstantClass.VALID_USER
        );
        Response loginRes = api.login(loginPayload);
        assertEquals(200, loginRes.statusCode(), "Login should succeed");
        String token = loginRes.jsonPath().getString("accessToken");
        assertNotNull(token, "Access token should not be null");

        // ===== Call /user/me =====
        Map<String, Object> requestPayload = Map.of(ConstantClass.FIELD_TOKEN, token);
        Response res = api.userMe(token);

        ReusableMethod.validateApiScenario(
                "User requests their own info with a valid token.",
                requestPayload,
                res,
                200,
                "id", "username", "email"
        );
        ReusableMethod.attachApiCall(requestPayload, res);
    }

    // -------------------------------
    // Positive: multiple calls
    // -------------------------------
    @Story("Positive Scenarios")
    @Test
    @Tag("test12")
    @DisplayName("TC12 - Multiple calls return same user info")
    void user_me_multiple_times_should_return_same_user() {

        Map<String, Object> loginPayload = TestDataManager.getDataAsMap(
                ConstantClass.DUMMYJSON,
                ConstantClass.LOGIN,
                ConstantClass.VALID_USER
        );

        Response loginRes = api.login(loginPayload);
        String token = loginRes.jsonPath().getString("accessToken");

        Map<String, Object> requestPayload = Map.of(ConstantClass.FIELD_TOKEN, token);

        Response firstResponse = api.userMe(token);
        ReusableMethod.validateApiScenario(
                "First call returns user info.",
                requestPayload,
                firstResponse,
                200,
                "id", "username"
        );
        ReusableMethod.attachApiCall(requestPayload, firstResponse);

        int expectedId = firstResponse.jsonPath().getInt(ConstantClass.FIELD_ID);
        String expectedUsername = firstResponse.jsonPath().getString(ConstantClass.FIELD_USERNAME);

        for (int i = 2; i <= 8; i++) {
            final int callNumber = i;
            Allure.step("Call #" + callNumber + " to /user/me", () -> {
                Response res = api.userMe(token);
                ReusableMethod.validateApiScenario(
                        "Repeated call returns same user info.",
                        requestPayload,
                        res,
                        200,
                        "id", "username"
                );
                ReusableMethod.attachApiCall(requestPayload, res);

                assertEquals(expectedId, res.jsonPath().getInt(ConstantClass.FIELD_ID));
                assertEquals(expectedUsername, res.jsonPath().getString(ConstantClass.FIELD_USERNAME));
            });
        }
    }

    // -------------------------------
    // Positive: ignore Accept-Encoding
    // -------------------------------
    @Story("Positive Scenarios")
    @Test
    @Tag("test13")
    @DisplayName("TC13 - Get user info without Accept-Encoding header")
    void user_me_without_accept_encoding_should_still_return_200() {

        // ===== Login to get fresh token =====
        Map<String, Object> loginPayload = TestDataManager.getDataAsMap(
                ConstantClass.DUMMYJSON,
                ConstantClass.LOGIN,
                ConstantClass.VALID_USER
        );
        Response loginRes = api.login(loginPayload);
        assertEquals(200, loginRes.statusCode(), "Login should succeed");
        String token = loginRes.jsonPath().getString("accessToken");
        assertNotNull(token, "Access token should not be null");

        // ===== Call /user/me with Accept-Encoding identity =====
        Response res = io.restassured.RestAssured.given()
                .spec(RequestSpecFactory.dummyJson())
                .header("Authorization", "Bearer " + token)
                .header(ConstantClass.FIELD_ACCEPT_ENCODING, "identity")
                .log().all()
                .when()
                .get(ApiPaths.USER_ME)
                .then()
                .log().all()
                .extract()
                .response();

        Map<String, Object> requestPayload = Map.of(
                ConstantClass.FIELD_TOKEN, token,
                ConstantClass.FIELD_ACCEPT_ENCODING, "identity"
        );

        ReusableMethod.validateApiScenario(
                "User calls /user/me with a valid token but sets Accept-Encoding to identity.",
                requestPayload,
                res,
                200,
                "id", "username"
        );
        ReusableMethod.attachApiCall(requestPayload, res);
    }


    // -------------------------------
    // Negative: expired token
    // -------------------------------
    @Story("Negative Scenarios")
    @Test
    @Tag("test9")
    @DisplayName("TC09 - Get user info with expired token")
    void user_me_expired_token_should_return_401() {
        String expiredToken = TestDataManager.getDataNode(
                ConstantClass.DUMMYJSON,
                ConstantClass.LOGIN,
                "expiredToken"
        ).asText();

        Map<String, Object> requestPayload = Map.of(ConstantClass.FIELD_TOKEN, expiredToken);
        Response res = api.userMe(expiredToken);

        ReusableMethod.validateApiScenario(
                "User attempts to call /user/me with an expired token.",
                requestPayload,
                res,
                401
        );

        ReusableMethod.attachApiCall(requestPayload, res);
    }

    // -------------------------------
    // Negative: invalid token
    // -------------------------------
    @Story("Negative Scenarios")
    @Test
    @Tag("test10")
    @DisplayName("TC10 - Get user info with invalid token")
    void user_me_invalid_token_should_return_401() {
        String invalidToken = TestDataManager.getDataNode(
                ConstantClass.DUMMYJSON,
                ConstantClass.LOGIN,
                "invalidToken"
        ).asText();

        Map<String, Object> requestPayload = Map.of(ConstantClass.FIELD_TOKEN, invalidToken);
        Response res = api.userMe(invalidToken);

        ReusableMethod.validateApiScenario(
                "User attempts to call /user/me with an invalid token.",
                requestPayload,
                res,
                401
        );

        ReusableMethod.attachApiCall(requestPayload, res);
    }
}

//package tests.dummyjson.auth;
//
//import clients.DummyJsonClient;
//import constant.ApiPaths;
//import constant.ConstantClass;
//import core.BaseApiTest;
//import core.RequestSpecFactory;
//import core.TestDataManager;
//import io.qameta.allure.*;
//import io.restassured.response.Response;
//import org.junit.jupiter.api.*;
//import org.slf4j.Logger;
//import utils.LoggerUtils;
//import utils.reusablemethod.ReusableMethod;
//
//import java.util.Map;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertNotNull;
//
//@TestInstance(TestInstance.Lifecycle.PER_CLASS)
//@Epic("DummyJson API")
//@Feature("User Me Endpoint")
//@DisplayName("DummyJsonUserMeTests")
//public class DummyJsonUserMeTests extends BaseApiTest {
//
//    private static final Logger log = LoggerUtils.getLogger(DummyJsonUserMeTests.class);
//    private final DummyJsonClient api = new DummyJsonClient();
//
//    // -------------------------------
//    // Positive: valid token
//    // -------------------------------
//    @Story("Positive Scenarios")
//    @Test
//    @Tag("test8")
//    @DisplayName("TC08 - Get user info with valid token")
//    void user_me_valid_token_should_return_200() {
//        String token = TestDataManager.getDataNode(
//                ConstantClass.DUMMYJSON,
//                ConstantClass.LOGIN,
//                ConstantClass.VALID_USER
//        ).asText();
//        log.info("TC08: Calling /user/me with token={}", token);
//
//        Response res = api.userMe(token);
//        Map<String, Object> requestPayload = Map.of(ConstantClass.FIELD_TOKEN, token);
//
//        ReusableMethod.attachApiCall(requestPayload, res);
//        ReusableMethod.attachBusinessSummary(
//                "User requests their own info with a valid token."
//        );
//    }
//
//    // -------------------------------
//    // Positive: multiple calls
//    // -------------------------------
//    @Story("Positive Scenarios")
//    @Test
//    @Tag("test12")
//    @DisplayName("TC12 - Multiple calls return same user info")
//    void user_me_multiple_times_should_return_same_user() {
//
//        // ===== LOGIN (Setup Step) =====
//        Map<String, Object> loginPayload = TestDataManager.getDataAsMap(
//                ConstantClass.DUMMYJSON,
//                ConstantClass.LOGIN,
//                ConstantClass.VALID_USER
//        );
//
//        Response loginRes = api.login(loginPayload);
//
//        assertEquals(200, loginRes.statusCode(), "Login should succeed");
//        String token = loginRes.jsonPath().getString("accessToken");
//        assertNotNull(token, "Access token should not be null");
//
//        Map<String, Object> requestPayload = Map.of(ConstantClass.FIELD_TOKEN, token);
//
//        // ===== FIRST CALL (Baseline) =====
//        Response firstResponse = api.userMe(token);
//
//        ReusableMethod.attachApiCall(requestPayload, firstResponse);
//        assertEquals(200, firstResponse.statusCode());
//
//        int expectedId = firstResponse.jsonPath().getInt(ConstantClass.FIELD_ID);
//        String expectedUsername = firstResponse.jsonPath().getString(ConstantClass.FIELD_USERNAME);
//
//        // ===== REMAINING CALLS =====
//        for (int i = 2; i <= 8; i++) {
//            final int callNumber = i;
//
//            Allure.step("Call #" + callNumber + " to /user/me", () -> {
//
//                Response res = api.userMe(token);
//                ReusableMethod.attachApiCall(requestPayload, res);
//
//                assertEquals(200, res.statusCode());
//
//                assertEquals(expectedId,
//                        res.jsonPath().getInt(ConstantClass.FIELD_ID),
//                        "User ID should remain consistent");
//
//                assertEquals(expectedUsername,
//                        res.jsonPath().getString(ConstantClass.FIELD_USERNAME),
//                        "Username should remain consistent");
//            });
//        }
//    }
//
//    // -------------------------------
//    // Positive: ignore Accept-Encoding
//    // -------------------------------
//    @Story("Positive Scenarios")
//    @Test
//    @Tag("test13")
//    @DisplayName("TC13 - Get user info without Accept-Encoding header")
//    void user_me_without_accept_encoding_should_still_return_200() {
//        String token = TestDataManager.getDataNode(
//                ConstantClass.DUMMYJSON,
//                ConstantClass.LOGIN,
//                ConstantClass.VALID_USER
//        ).asText();
//        log.info("TC13: Calling /user/me without Accept-Encoding header with token={}", token);
//
//        Response res = io.restassured.RestAssured.given()
//                .spec(RequestSpecFactory.dummyJson())
//                .header("Authorization", "Bearer " + token)
//                .header(ConstantClass.FIELD_ACCEPT_ENCODING, "identity")
//                .log().all()
//                .when()
//                .get(ApiPaths.USER_ME)
//                .then()
//                .log().all()
//                .extract()
//                .response();
//
//        Map<String, Object> requestPayload = Map.of(
//                ConstantClass.FIELD_TOKEN, token,
//                ConstantClass.FIELD_ACCEPT_ENCODING, "identity"
//        );
//
//        ReusableMethod.attachApiCall(requestPayload, res);
//        ReusableMethod.attachBusinessSummary(
//                "User calls /user/me with a valid token but sets Accept-Encoding to identity."
//        );
//    }
//
//    // -------------------------------
//    // Negative: expired token
//    // -------------------------------
//    @Story("Negative Scenarios")
//    @Test
//    @Tag("test9")
//    @DisplayName("TC09 - Get user info with expired token")
//    void user_me_expired_token_should_return_401() {
//        String expiredToken = TestDataManager.getDataNode(
//                ConstantClass.DUMMYJSON,
//                ConstantClass.LOGIN,
//                "expiredToken"
//        ).asText();
//        log.info("TC09: Calling /user/me with expired token={}", expiredToken);
//
//        Response res = api.userMe(expiredToken);
//        Map<String, Object> requestPayload = Map.of(ConstantClass.FIELD_TOKEN, expiredToken);
//
//        ReusableMethod.attachApiCall(requestPayload, res);
//        ReusableMethod.attachBusinessSummary(
//                "User attempts to call /user/me with an expired token."
//        );
//    }
//
//    // -------------------------------
//    // Negative: invalid token
//    // -------------------------------
//    @Story("Negative Scenarios")
//    @Test
//    @Tag("test10")
//    @DisplayName("TC10 - Get user info with invalid token")
//    void user_me_invalid_token_should_return_401() {
//        String invalidToken = TestDataManager.getDataNode(
//                ConstantClass.DUMMYJSON,
//                ConstantClass.LOGIN,
//                "invalidToken"
//        ).asText();
//        log.info("TC10: Calling /user/me with invalid token={}", invalidToken);
//
//        Response res = api.userMe(invalidToken);
//        Map<String, Object> requestPayload = Map.of(ConstantClass.FIELD_TOKEN, invalidToken);
//
//        ReusableMethod.attachApiCall(requestPayload, res);
//        ReusableMethod.attachBusinessSummary(
//                "User attempts to call /user/me with an invalid token."
//        );
//    }
//}