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
        ReusableMethod.validateRequestSection(requestPayload, ConstantClass.FIELD_TOKEN);
        ReusableMethod.validateStatusSection(res, 200);
        ReusableMethod.validateResponseSection(res,
                ConstantClass.FIELD_ID, ConstantClass.FIELD_USERNAME);
    }

    @Story("Positive Scenarios")
    @Test
    @Tag("test12")
    @DisplayName("TC12 - Multiple calls return same user info")
    void user_me_multiple_times_should_return_same_user() {
        String token = TokenHelper.getValidUserToken();
        log.info("TC12: Calling /user/me multiple times with token={}", token);

        Map<String, Object> requestPayload = Map.of(ConstantClass.FIELD_TOKEN, token);

        Allure.step("API Request / Response for multiple calls", () -> {
            Response first = api.userMe(token);
            ReusableMethod.attachApiCall(requestPayload, first);
            ReusableMethod.validateStatusSection(first, 200);

            Map<String, Object> expectedFields = Map.of(
                    ConstantClass.FIELD_ID, first.jsonPath().getInt(ConstantClass.FIELD_ID),
                    ConstantClass.FIELD_USERNAME, first.jsonPath().getString(ConstantClass.FIELD_USERNAME)
            );

            ReusableMethod.validateResponseFields(first, expectedFields);

            for (int i = 2; i <= 8; i++) {
                Response next = api.userMe(token);
                ReusableMethod.attachApiCall(requestPayload, next);
                ReusableMethod.validateStatusSection(next, 200);
                ReusableMethod.validateResponseFields(next, expectedFields);
            }
        });
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
        ReusableMethod.validateRequestSection(requestPayload,
                ConstantClass.FIELD_TOKEN, ConstantClass.FIELD_ACCEPT_ENCODING);
        ReusableMethod.validateStatusSection(res, 200);
        ReusableMethod.validateResponseSection(res,
                ConstantClass.FIELD_ID, ConstantClass.FIELD_USERNAME);
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
        ReusableMethod.validateStatusSection(res, 401);
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
        ReusableMethod.validateStatusSection(res, 401);
    }
}