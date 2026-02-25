package tests.dummyjson.auth;

import clients.DummyJsonClient;
import constant.ConstantClass;
import core.BaseApiTest;
import core.TestDataManager;
import io.qameta.allure.*;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.*;
import utils.reusablemethod.ReusableMethod;

import java.util.Map;

@Epic("DummyJson API")
@Feature("DummyJsonAuthTests")
@DisplayName("DummyJsonAuthTests")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class DummyJsonAuthTests extends BaseApiTest {

    private final DummyJsonClient api = new DummyJsonClient();

    @Story(ConstantClass.STORY_LOGIN_SUCCESS)
    @Test
    @Tag("auth")
    @DisplayName(ConstantClass.TEST1_NAME)
    void login_success_should_return_200() {

        Map<String, Object> requestPayload =
                TestDataManager.getDataAsMap(
                        ConstantClass.DUMMYJSON,
                        ConstantClass.LOGIN,
                        ConstantClass.VALID_USER
                );

        Response res = api.login(requestPayload);

        ReusableMethod.attachApiCall(requestPayload, res);

        ReusableMethod.validateRequestSection(requestPayload,
                ConstantClass.FIELD_USERNAME, ConstantClass.FIELD_PASSWORD);
        ReusableMethod.validateStatusSection(res, HttpStatus.SC_OK);
        ReusableMethod.validateResponseSection(res,
                ConstantClass.FIELD_ACCESS_TOKEN);
    }

    @Story(ConstantClass.STORY_LOGIN_SUCCESS)
    @Test
    @Tag("auth")
    @DisplayName("TC02 - Login using framework baseUrl should work")
    void login_using_framework_baseUrl_should_work() {

        Map<String, Object> requestPayload =
                TestDataManager.getDataAsMap(
                        ConstantClass.DUMMYJSON,
                        ConstantClass.LOGIN,
                        ConstantClass.VALID_USER
                );

        Response res = api.login(requestPayload);

        ReusableMethod.attachApiCall(requestPayload, res);

        ReusableMethod.validateRequestSection(requestPayload,
                ConstantClass.FIELD_USERNAME, ConstantClass.FIELD_PASSWORD);
        ReusableMethod.validateStatusSection(res, HttpStatus.SC_OK);
        ReusableMethod.validateResponseSection(res,
                ConstantClass.FIELD_ACCESS_TOKEN);
    }

    @Story("Negative Scenarios")
    @Test
    @Tag("auth")
    @DisplayName("TC03 - Missing password should return 400")
    void missing_password_should_return_400() {

        Map<String, Object> requestPayload =
                TestDataManager.getDataAsMap(
                        ConstantClass.DUMMYJSON,
                        ConstantClass.LOGIN,
                        ConstantClass.MISSING_PASSWORD
                );

        Response res = api.login(requestPayload);

        ReusableMethod.attachApiCall(requestPayload, res);

        ReusableMethod.validateRequestSection(requestPayload,
                ConstantClass.FIELD_USERNAME);
        ReusableMethod.validateStatusSection(res, HttpStatus.SC_BAD_REQUEST);
        ReusableMethod.validateResponseSection(res);
    }

    @Story("Negative Scenarios")
    @Test
    @Tag("auth")
    @DisplayName("TC04 - Missing username should return 400")
    void missing_username_should_return_400() {

        Map<String, Object> requestPayload =
                TestDataManager.getDataAsMap(
                        ConstantClass.DUMMYJSON,
                        ConstantClass.LOGIN,
                        ConstantClass.MISSING_USERNAME
                );

        Response res = api.login(requestPayload);

        ReusableMethod.attachApiCall(requestPayload, res);

        ReusableMethod.validateRequestSection(requestPayload,
                ConstantClass.FIELD_PASSWORD);
        ReusableMethod.validateStatusSection(res, HttpStatus.SC_BAD_REQUEST);
        ReusableMethod.validateResponseSection(res);
    }

    @Story("Negative Scenarios")
    @Test
    @Tag("auth")
    @DisplayName("TC05 - Invalid username type should return 400")
    void username_integer_should_return_400() {

        Map<String, Object> requestPayload =
                TestDataManager.getDataAsMap(
                        ConstantClass.DUMMYJSON,
                        ConstantClass.LOGIN,
                        ConstantClass.INVALID_USERNAME_TYPE
                );

        Response res = api.login(requestPayload);

        ReusableMethod.attachApiCall(requestPayload, res);

        ReusableMethod.validateRequestSection(requestPayload,
                ConstantClass.FIELD_USERNAME, ConstantClass.FIELD_PASSWORD);
        ReusableMethod.validateStatusSection(res, HttpStatus.SC_BAD_REQUEST);
        ReusableMethod.validateResponseSection(res);
    }

    @Story("Negative Scenarios")
    @Test
    @Tag("auth")
    @DisplayName("TC06 - Empty body should return 400")
    void empty_body_should_return_error() {

        Map<String, Object> requestPayload =
                TestDataManager.getDataAsMap(
                        ConstantClass.DUMMYJSON,
                        ConstantClass.LOGIN,
                        ConstantClass.EMPTY_BODY
                );

        Response res = api.login(requestPayload);

        ReusableMethod.attachApiCall(requestPayload, res);

        ReusableMethod.validateRequestSection(requestPayload);
        ReusableMethod.validateStatusSection(res, HttpStatus.SC_BAD_REQUEST);
        ReusableMethod.validateResponseSection(res);
    }
}