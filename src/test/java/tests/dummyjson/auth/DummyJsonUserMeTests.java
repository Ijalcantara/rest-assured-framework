package tests.dummyjson.auth;

import clients.DummyJsonClient;
import constant.EndpointConstant;
import constant.ConstantClass;
import core.BaseApiTest;
import core.RequestSpecFactory;
import core.TestDataManager;
import io.qameta.allure.*;
import io.restassured.response.Response;
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import utils.LoggerUtils;
import utils.ApiAllureUtil;

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
        Response loginRes = api.login(loginPayload);
        assertEquals(200, loginRes.statusCode(), "Login should succeed");
        String token = loginRes.jsonPath().getString("accessToken");
        assertNotNull(token, "Access token should not be null");

        // ===== Call /user/me =====
        Map<String, Object> requestPayload = Map.of(ConstantClass.FIELD_TOKEN, token);
        Response res = api.userMe(token);

        ApiAllureUtil.validateApiScenario(
                "User requests their own info with a valid token.",
                requestPayload,
                res,
                200,
                "id", "username", "email"
        );
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

        Response loginRes = api.login(loginPayload);
        String token = loginRes.jsonPath().getString("accessToken");

        Map<String, Object> requestPayload = Map.of(ConstantClass.FIELD_TOKEN, token);

        Response firstResponse = api.userMe(token);
        ApiAllureUtil.validateApiScenario(
                "First call returns user info.",
                requestPayload,
                firstResponse,
                200,
                "id", "username"
        );
        ApiAllureUtil.attachApiCall(requestPayload, firstResponse);

        int expectedId = firstResponse.jsonPath().getInt(ConstantClass.FIELD_ID);
        String expectedUsername = firstResponse.jsonPath().getString(ConstantClass.FIELD_USERNAME);

        for (int i = 2; i <= 8; i++) {
            final int callNumber = i;
            Allure.step("Call #" + callNumber + " to /user/me", () -> {
                Response res = api.userMe(token);
                ApiAllureUtil.validateApiScenario(
                        "Repeated call returns same user info.",
                        requestPayload,
                        res,
                        200,
                        "id", "username"
                );
                ApiAllureUtil.attachApiCall(requestPayload, res);

                assertEquals(expectedId, res.jsonPath().getInt(ConstantClass.FIELD_ID));
                assertEquals(expectedUsername, res.jsonPath().getString(ConstantClass.FIELD_USERNAME));
            });
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
                .get(EndpointConstant.USER_ME)
                .then()
                .log().all()
                .extract()
                .response();

        Map<String, Object> requestPayload = Map.of(
                ConstantClass.FIELD_TOKEN, token,
                ConstantClass.FIELD_ACCEPT_ENCODING, "identity"
        );

        ApiAllureUtil.validateApiScenario(
                "User calls /user/me with a valid token but sets Accept-Encoding to identity.",
                requestPayload,
                res,
                200,
                "id", "username"
        );
        ApiAllureUtil.attachApiCall(requestPayload, res);
    }

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

        ApiAllureUtil.validateApiScenario(
                "User attempts to call /user/me with an expired token.",
                requestPayload,
                res,
                401
        );

        ApiAllureUtil.attachApiCall(requestPayload, res);
    }

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

        ApiAllureUtil.validateApiScenario(
                "User attempts to call /user/me with an invalid token.",
                requestPayload,
                res,
                401
        );

        ApiAllureUtil.attachApiCall(requestPayload, res);
    }
}