package tests.dummyjson.auth;

import clients.DummyJsonClient;
import constant.ConstantClass;
import core.BaseApiTest;
import manager.TestDataManager;
import io.qameta.allure.*;
import io.restassured.response.Response;
import org.junit.jupiter.api.*;
import utils.ApiAllureUtil;

import java.util.Map;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Epic("DummyJson API")
@Feature("DummyJsonAuthTests")
@DisplayName("DummyJsonAuthTests")
class DummyJsonAuthTests extends BaseApiTest {

    private final DummyJsonClient api = new DummyJsonClient();

    @Story(ConstantClass.STORY_LOGIN_SUCCESS)
    @Test
    @Tag("auth")
    @DisplayName("TC01 - Login success")
    void login_success_should_return_200() {

        Map<String, Object> requestPayload = TestDataManager.getNestedDataAsMap(
                ConstantClass.DUMMYJSON,
                ConstantClass.LOGIN,
                ConstantClass.VALID_USER
        );

        Response res = api.login(requestPayload);

        ApiAllureUtil.logScenario("User logs in with valid credentials.");
        // ✅ Log request payload in Allure
        ApiAllureUtil.logRequestPayload(requestPayload);

        ApiAllureUtil.validateStatusCode(res, 200);
        ApiAllureUtil.validateResponseBody(res, "accessToken", "username");

        ApiAllureUtil.attachApiCall(requestPayload, res);
    }

    @Story(ConstantClass.STORY_LOGIN_SUCCESS)
    @Test
    @Tag("auth")
    @DisplayName("TC02 - Login using framework baseUrl should work")
    void login_using_framework_baseUrl_should_work() {

        Map<String, Object> requestPayload = TestDataManager.getNestedDataAsMap(
                ConstantClass.DUMMYJSON,
                ConstantClass.LOGIN,
                ConstantClass.VALID_USER
        );

        Response res = api.login(requestPayload);

        ApiAllureUtil.logScenario(
                "User logs in using the framework's baseUrl with valid credentials."
        );
        ApiAllureUtil.logRequestPayload(requestPayload);

        ApiAllureUtil.validateStatusCode(res, 200);
        ApiAllureUtil.validateResponseBody(res, "accessToken", "username");

        ApiAllureUtil.attachApiCall(requestPayload, res);
    }

    @Story("Negative Scenarios")
    @Test
    @Tag("auth")
    @DisplayName("TC03 - Missing password should return 400")
    void missing_password_should_return_400() {

        Map<String, Object> requestPayload = TestDataManager.getNestedDataAsMap(
                ConstantClass.DUMMYJSON,
                ConstantClass.LOGIN,
                ConstantClass.MISSING_PASSWORD
        );

        Response res = api.login(requestPayload);

        ApiAllureUtil.logScenario(
                "User attempts to login without providing a password."
        );
        ApiAllureUtil.logRequestPayload(requestPayload);

        ApiAllureUtil.validateStatusCode(res, 400);
        ApiAllureUtil.validateResponseBody(res);

        ApiAllureUtil.attachApiCall(requestPayload, res);
    }

    @Story("Negative Scenarios")
    @Test
    @Tag("auth")
    @DisplayName("TC04 - Missing username should return 400")
    void missing_username_should_return_400() {

        Map<String, Object> requestPayload = TestDataManager.getNestedDataAsMap(
                ConstantClass.DUMMYJSON,
                ConstantClass.LOGIN,
                ConstantClass.MISSING_USERNAME
        );

        Response res = api.login(requestPayload);

        ApiAllureUtil.logScenario(
                "User attempts to login without providing a username."
        );
        ApiAllureUtil.logRequestPayload(requestPayload);

        ApiAllureUtil.validateStatusCode(res, 400);
        ApiAllureUtil.validateResponseBody(res);

        ApiAllureUtil.attachApiCall(requestPayload, res);
    }

    @Story("Negative Scenarios")
    @Test
    @Tag("auth")
    @DisplayName("TC05 - Login should fail when username format is invalid")
    void username_integer_should_return_400() {

        Map<String, Object> requestPayload = TestDataManager.getNestedDataAsMap(
                ConstantClass.DUMMYJSON,
                ConstantClass.LOGIN,
                ConstantClass.INVALID_USERNAME_TYPE
        );

        Response res = api.login(requestPayload);

        ApiAllureUtil.logScenario(
                "User attempts to login using a numeric value instead of text in the username field."
        );
        ApiAllureUtil.logRequestPayload(requestPayload);

        ApiAllureUtil.validateStatusCode(res, 400);
        ApiAllureUtil.validateResponseBody(res);

        ApiAllureUtil.attachApiCall(requestPayload, res);
    }

    @Story("Negative Scenarios")
    @Test
    @Tag("auth")
    @DisplayName("TC06 - Empty body should return 400")
    void empty_body_should_return_error() {

        Map<String, Object> requestPayload = TestDataManager.getNestedDataAsMap(
                ConstantClass.DUMMYJSON,
                ConstantClass.LOGIN,
                ConstantClass.EMPTY_BODY
        );

        Response res = api.login(requestPayload);

        ApiAllureUtil.logScenario(
                "User sends an empty body for login request."
        );
        ApiAllureUtil.logRequestPayload(requestPayload);

        ApiAllureUtil.validateStatusCode(res, 400);
        ApiAllureUtil.validateResponseBody(res);

        ApiAllureUtil.attachApiCall(requestPayload, res);
    }
}