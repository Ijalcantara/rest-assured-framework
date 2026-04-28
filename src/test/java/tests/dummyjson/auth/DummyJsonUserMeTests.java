package tests.dummyjson.auth;

import clients.DummyJsonClient;
import constant.ConstantClass;
import core.BaseApiTest;
import io.qameta.allure.*;
import io.restassured.response.Response;
import manager.TestDataManager;
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.AuthTokenUtil;

import utils.ApiAllureUtil;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Epic("DummyJson API")
@Feature("User Me Endpoint")
@DisplayName("DummyJsonUserMeTests")
@Tag("dummyjson")
public class DummyJsonUserMeTests extends BaseApiTest {

    private final DummyJsonClient api = new DummyJsonClient();
    private static final Logger log = LoggerFactory.getLogger(BaseApiTest.class);

    @Story("Positive Scenarios")
    @Test
    @Tag("test8")
    @DisplayName("TC08 - Get user info with valid token")
    void user_me_valid_token_should_return_200() {
        Map<String, Object> loginPayload = TestDataManager.getNestedDataAsMap(
                ConstantClass.DUMMYJSON,
                ConstantClass.LOGIN,
                ConstantClass.VALID_USER
        );

        Response loginResponse = api.login(loginPayload);

        String token = AuthTokenUtil.getToken(loginResponse, "accessToken");
        log.info("This is the response: " + loginResponse.asPrettyString());
//        String token = AuthUtil.getToken();

        Map<String, Object> requestPayload = new HashMap<>(
                Map.of(ConstantClass.FIELD_TOKEN, token)
        );

        Response res = api.userMe(token);

        ApiAllureUtil.logScenario("User requests their own info with a valid token.");
        ApiAllureUtil.validateStatusCode(res, 200);
        ApiAllureUtil.validateResponseBody(res, "id", "username", "email");

        ApiAllureUtil.attachApiCall(requestPayload, res);
    }

    @Story("Positive Scenarios")
    @Test
    @Tag("test12")
    @DisplayName("TC12 - Multiple calls return same user info")
    void user_me_multiple_times_should_return_same_user() {

        Map<String, Object> loginPayload = TestDataManager.getNestedDataAsMap(
                ConstantClass.DUMMYJSON,
                ConstantClass.LOGIN,
                ConstantClass.VALID_USER
        );

        Response loginResponse = api.login(loginPayload);

        String token = AuthTokenUtil.getToken(loginResponse, "accessToken");
        log.info("This is the response: " + loginResponse.asPrettyString());

        Map<String, Object> requestPayload = new HashMap<>(
                Map.of(ConstantClass.FIELD_TOKEN, token)
        );

        // First call
        Response firstResponse = api.userMe(token);
        ApiAllureUtil.validateStatusCode(firstResponse, 200);
        ApiAllureUtil.validateResponseBody(firstResponse, "id", "username");

        int expectedId = firstResponse.jsonPath().getInt(ConstantClass.FIELD_ID);
        String expectedUsername = firstResponse.jsonPath().getString(ConstantClass.FIELD_USERNAME);

        // Repeat calls
        for (int i = 2; i <= 8; i++) {
            Response res = api.userMe(token);
            Allure.step("Call #" + i + " - Validate response");

            ApiAllureUtil.validateStatusCode(res, 200);
            ApiAllureUtil.validateResponseBody(res, "id", "username");

            assertEquals(expectedId, res.jsonPath().getInt(ConstantClass.FIELD_ID));
            assertEquals(expectedUsername, res.jsonPath().getString(ConstantClass.FIELD_USERNAME));

            ApiAllureUtil.attachApiCall(new HashMap<>(requestPayload), res);
        }
    }

    @Story("Positive Scenarios")
    @Test
    @Tag("test13")
    @DisplayName("TC13 - Get user info without Accept-Encoding header")
    void user_me_without_accept_encoding_should_still_return_200() {
        Map<String, Object> loginPayload = TestDataManager.getNestedDataAsMap(
                ConstantClass.DUMMYJSON,
                ConstantClass.LOGIN,
                ConstantClass.VALID_USER
        );

        Response loginResponse = api.login(loginPayload);

        String token = AuthTokenUtil.getToken(loginResponse, "accessToken");
        log.info("This is the response: " + loginResponse.asPrettyString());
//        String token = AuthUtil.getToken();

        Response res = api.userMe(token);

        ApiAllureUtil.logScenario("User calls /user/me without Accept-Encoding header.");
        ApiAllureUtil.validateStatusCode(res, 200);
        ApiAllureUtil.validateResponseBody(res, "id", "username");

        ApiAllureUtil.attachApiCall(new HashMap<>(Map.of("token", token)), res);
    }

    @Story("Negative Scenarios")
    @Test
    @DisplayName("TC09 - Get user info with expired token")
    void user_me_expired_token_should_return_401() {

        String expiredToken = TestDataManager
                .getDataNode(ConstantClass.DUMMYJSON, ConstantClass.LOGIN, "expiredToken")
                .asText();

        Response res = api.userMe(expiredToken);

        log.info("This is the response: " + res.asPrettyString());
        ApiAllureUtil.validateStatusCode(res, 401);
    }

    @Story("Negative Scenarios")
    @Test
    @Tag("test10")
    @DisplayName("TC10 - Get user info with invalid token")
    void user_me_invalid_token_should_return_401() {

        String invalidToken = TestDataManager
                .getDataNode(ConstantClass.DUMMYJSON, ConstantClass.LOGIN, "invalidToken")
                .asText();

        Response res = api.userMe(invalidToken);

        ApiAllureUtil.validateStatusCode(res, 401);
    }
}