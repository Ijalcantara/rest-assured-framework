package tests.dummyjson.auth;

import clients.DummyJsonClient;
import constant.ConstantClass;
import core.BaseApiTest;
import core.RequestSpecFactory;
import core.TestDataManager;
import io.qameta.allure.*;
import io.restassured.response.Response;
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import utils.LoggerUtils;
import utils.TokenHelper;
import utils.reusablemethod.ReusableMethod;

import java.util.Map;

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
        String token = TokenHelper.getValidUserToken();
        log.info("TC08: Calling /user/me with token={}", token);

        Response res = api.userMe(token);
        Map<String, Object> requestPayload = Map.of(ConstantClass.FIELD_TOKEN, token);

        ReusableMethod.attachApiCall(requestPayload, res);
        ReusableMethod.attachBusinessSummary(
                "User requests their own info with a valid token.",
                "System should return user details with 200 OK status, including user ID and username.",
                res
        );
    }

    @Story("Positive Scenarios")
    @Test
    @Tag("test12")
    @DisplayName("TC12 - Multiple calls return same user info")
    void user_me_multiple_times_should_return_same_user() {
        String token = TokenHelper.getValidUserToken();
        log.info("TC12: Calling /user/me multiple times with token={}", token);

        Map<String, Object> requestPayload = Map.of(ConstantClass.FIELD_TOKEN, token);

        // Perform 8 calls as separate top-level Allure steps
        for (int i = 1; i <= 8; i++) {
            final int callNumber = i; // needed for lambda
            Allure.step("Call #" + callNumber + " to /user/me", () -> {

                Response res = api.userMe(token);
                ReusableMethod.attachApiCall(requestPayload, res);
                ReusableMethod.attachBusinessSummary(
                        callNumber == 1
                                ? "User calls /user/me for the first time with a valid token."
                                : "User calls /user/me again with the same valid token.",
                        "System should return the same user info as previous call.",
                        res
                );

                if (callNumber == 1) {
                    Map<String, Object> expectedFields = Map.of(
                            ConstantClass.FIELD_ID, res.jsonPath().getInt(ConstantClass.FIELD_ID),
                            ConstantClass.FIELD_USERNAME, res.jsonPath().getString(ConstantClass.FIELD_USERNAME)
                    );
                    ReusableMethod.storeExpectedFields("userMe", expectedFields);
                } else {
                    Map<String, Object> expectedFields = ReusableMethod.getExpectedFields("userMe");
                    ReusableMethod.validateResponseFields(res, expectedFields);
                }
            });
        }
    }

    @Story("Positive Scenarios")
    @Test
    @Tag("test13")
    @DisplayName("TC13 - Get user info without Accept-Encoding header")
    void user_me_without_accept_encoding_should_still_return_200() {
        String token = TokenHelper.getValidUserToken();
        log.info("TC13: Calling /user/me without Accept-Encoding header with token={}", token);

        Response res = io.restassured.RestAssured.given()
                .spec(RequestSpecFactory.dummyJson())
                .header("Authorization", "Bearer " + token)
                .header(ConstantClass.FIELD_ACCEPT_ENCODING, "identity")
                .log().all()
                .when()
                .get("/user/me")
                .then()
                .log().all()
                .extract()
                .response();

        Map<String, Object> requestPayload = Map.of(
                ConstantClass.FIELD_TOKEN, token,
                ConstantClass.FIELD_ACCEPT_ENCODING, "identity"
        );

        ReusableMethod.attachApiCall(requestPayload, res);
        ReusableMethod.attachBusinessSummary(
                "User calls /user/me with a valid token but sets Accept-Encoding to identity.",
                "System should ignore the Accept-Encoding header and return user details with 200 OK.",
                res
        );
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
        log.info("TC09: Calling /user/me with expired token={}", expiredToken);

        Response res = api.userMe(expiredToken);
        Map<String, Object> requestPayload = Map.of(ConstantClass.FIELD_TOKEN, expiredToken);

        ReusableMethod.attachApiCall(requestPayload, res);
        ReusableMethod.attachBusinessSummary(
                "User attempts to call /user/me with an expired token.",
                "System should reject the request and return 401 Unauthorized.",
                res
        );
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
        log.info("TC10: Calling /user/me with invalid token={}", invalidToken);

        Response res = api.userMe(invalidToken);
        Map<String, Object> requestPayload = Map.of(ConstantClass.FIELD_TOKEN, invalidToken);

        ReusableMethod.attachApiCall(requestPayload, res);
        ReusableMethod.attachBusinessSummary(
                "User attempts to call /user/me with an invalid token.",
                "System should reject the request and return 401 Unauthorized.",
                res
        );
    }
}