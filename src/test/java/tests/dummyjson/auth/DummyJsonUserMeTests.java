package tests.dummyjson.auth;

import clients.DummyJsonClient;
import constant.EndpointConstant;
import constant.ConstantClass;
import core.BaseApiTest;
import core.RequestSpecFactory;
import manager.TestDataManager;
import io.qameta.allure.*;
import io.restassured.response.Response;
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import utils.LoggerUtils;
import utils.ApiAllureUtil;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Epic("DummyJson API")
@Feature("User Me Endpoint")
@DisplayName("DummyJsonUserMeTests")
public class DummyJsonUserMeTests extends BaseApiTest {

    private static final Logger log =
            LoggerUtils.getLogger(DummyJsonUserMeTests.class);

    private final DummyJsonClient api = new DummyJsonClient();

    // =====================================================
    // Helper Method (Cleaner Login Reuse)
    // =====================================================
    private String getValidToken() {
        Map<String, Object> loginPayload =
                TestDataManager.getNestedDataAsMap(
                        ConstantClass.DUMMYJSON,
                        ConstantClass.LOGIN,
                        ConstantClass.VALID_USER
                );

        Response loginRes = api.login(loginPayload);

        ApiAllureUtil.validateStatusCode(loginRes, 200);

        String token = loginRes.jsonPath().getString("accessToken");
        assertNotNull(token, "Access token should not be null");

        return token;
    }

    // =====================================================
    // TC08 - Valid Token
    // =====================================================
    @Story("Positive Scenarios")
    @Test
    @Tag("test8")
    @DisplayName("TC08 - Get user info with valid token")
    void user_me_valid_token_should_return_200() {

        String token = getValidToken();

        Map<String, Object> requestPayload =
                Map.of(ConstantClass.FIELD_TOKEN, token);

        Response res = api.userMe(token);

        ApiAllureUtil.logScenario(
                "User requests their own info with a valid token."
        );
        ApiAllureUtil.validateStatusCode(res, 200);
        ApiAllureUtil.validateResponseBody(res, "id", "username", "email");

        ApiAllureUtil.attachApiCall(requestPayload, res);
    }

    // =====================================================
    // TC12 - Multiple Calls Consistency
    // =====================================================
    @Story("Positive Scenarios")
    @Test
    @Tag("test12")
    @DisplayName("TC12 - Multiple calls return same user info")
    void user_me_multiple_times_should_return_same_user() {

        String token = getValidToken();

        Map<String, Object> requestPayload =
                Map.of(ConstantClass.FIELD_TOKEN, token);

        ApiAllureUtil.logScenario(
                "Repeated calls to /user/me should return consistent user info."
        );

        // First Call
        Response firstResponse = api.userMe(token);
        ApiAllureUtil.validateStatusCode(firstResponse, 200);
        ApiAllureUtil.validateResponseBody(firstResponse, "id", "username");
        ApiAllureUtil.attachApiCall(requestPayload, firstResponse);

        int expectedId =
                firstResponse.jsonPath().getInt(ConstantClass.FIELD_ID);
        String expectedUsername =
                firstResponse.jsonPath().getString(ConstantClass.FIELD_USERNAME);

        // Repeated Calls
        for (int i = 2; i <= 8; i++) {

            Response res = api.userMe(token);

            final int callNumber = i;

            Allure.step("Call #" + callNumber + " - Validate response");

            ApiAllureUtil.validateStatusCode(res, 200);
            ApiAllureUtil.validateResponseBody(res, "id", "username");

            assertEquals(expectedId,
                    res.jsonPath().getInt(ConstantClass.FIELD_ID));

            assertEquals(expectedUsername,
                    res.jsonPath().getString(ConstantClass.FIELD_USERNAME));

            ApiAllureUtil.attachApiCall(requestPayload, res);
        }
    }

    // =====================================================
    // TC13 - Without Accept-Encoding
    // =====================================================
    @Story("Positive Scenarios")
    @Test
    @Tag("test13")
    @DisplayName("TC13 - Get user info without Accept-Encoding header")
    void user_me_without_accept_encoding_should_still_return_200() {

        String token = getValidToken();

        Response res = io.restassured.RestAssured.given()
                .spec(RequestSpecFactory.dummyJson())
                .header("Authorization", "Bearer " + token)
                .header(ConstantClass.FIELD_ACCEPT_ENCODING, "identity")
                .when()
                .get(EndpointConstant.USER_ME)
                .then()
                .extract()
                .response();

        Map<String, Object> requestPayload = Map.of(
                ConstantClass.FIELD_TOKEN, token,
                ConstantClass.FIELD_ACCEPT_ENCODING, "identity"
        );

        ApiAllureUtil.logScenario(
                "User calls /user/me with Accept-Encoding set to identity."
        );
        ApiAllureUtil.validateStatusCode(res, 200);
        ApiAllureUtil.validateResponseBody(res, "id", "username");

        ApiAllureUtil.attachApiCall(requestPayload, res);
    }

    // =====================================================
    // TC09 - Expired Token
    // =====================================================
    @Story("Negative Scenarios")
    @Test
    @Tag("test9")
    @DisplayName("TC09 - Get user info with expired token")
    void user_me_expired_token_should_return_401() {

        String expiredToken =
                TestDataManager.getDataNode(
                        ConstantClass.DUMMYJSON,
                        ConstantClass.LOGIN,
                        "expiredToken"
                ).asText();

        Map<String, Object> requestPayload =
                Map.of(ConstantClass.FIELD_TOKEN, expiredToken);

        Response res = api.userMe(expiredToken);

        ApiAllureUtil.logScenario(
                "User attempts to call /user/me with an expired token."
        );
        ApiAllureUtil.validateStatusCode(res, 401);
        ApiAllureUtil.validateResponseBody(res);

        ApiAllureUtil.attachApiCall(requestPayload, res);
    }

    // =====================================================
    // TC10 - Invalid Token
    // =====================================================
    @Story("Negative Scenarios")
    @Test
    @Tag("test10")
    @DisplayName("TC10 - Get user info with invalid token")
    void user_me_invalid_token_should_return_401() {

        String invalidToken =
                TestDataManager.getDataNode(
                        ConstantClass.DUMMYJSON,
                        ConstantClass.LOGIN,
                        "invalidToken"
                ).asText();

        Map<String, Object> requestPayload =
                Map.of(ConstantClass.FIELD_TOKEN, invalidToken);

        Response res = api.userMe(invalidToken);

        ApiAllureUtil.logScenario(
                "User attempts to call /user/me with an invalid token."
        );
        ApiAllureUtil.validateStatusCode(res, 401);
        ApiAllureUtil.validateResponseBody(res);

        ApiAllureUtil.attachApiCall(requestPayload, res);
    }
}