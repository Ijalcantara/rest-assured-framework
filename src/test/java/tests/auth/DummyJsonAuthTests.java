package tests.auth;

import clients.DummyJsonClient;
import core.BaseApiTest;
import core.TestDataManager;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import utils.LoggerUtils;
import utils.reusablemethod.ReusableMethod;

import java.util.Map;

import static constant.ConstantClass.*;
import static org.junit.jupiter.api.Assertions.*;

@Epic("DummyJson API")
@Feature("Authentication")
@DisplayName("DummyJsonAuthTests")
public class DummyJsonAuthTests extends BaseApiTest {

    private final DummyJsonClient api = new DummyJsonClient();

    @Test
    @Tag("test1")
    @Story("Login Success with valid credentials")
    @Description("Test verifies successful login returns 200 and token without password")
    void login_success_should_return_200_and_no_password() {
        //String testName = "Test1 - Login Success";
        ReusableMethod.logTestStart(TEST1_NAME);

        Map<String, Object> user = TestDataManager.getDataAsMap(DUMMYJSON, LOGIN, VALID_USER);

        ReusableMethod.logRequest(LOGIN_CREDENTIALS_TXT, user);

        Response res = ReusableMethod.api.login(user);

        ReusableMethod.logResponse(res);
        ReusableMethod.assertLoginResponse(res, HttpStatus.SC_OK, true);

        ReusableMethod.logTestEnd(TEST1_NAME);
    }

    @Test
    @Tag("test2")
    @Story("Login Fail - Missing Password")
    @Description("Test verifies login without password returns 400 with proper message")
    void missing_password_should_return_400_and_message() {
        String testName = "Test2 - Missing Password";
        ReusableMethod.logTestStart(testName);

        Map<String, Object> user = TestDataManager.getDataAsMap(DUMMYJSON, LOGIN, MISSING_PASSWORD);

        ReusableMethod.logRequest(LOGIN_CREDENTIALS_TXT, user);

        Response res = api.login(user);
        ReusableMethod.logResponse(res);

        assertEquals(HttpStatus.SC_BAD_REQUEST, res.statusCode());

        String expectedMsg = (String) TestDataManager.getDataNode(DUMMYJSON, EXPECTED_MESSAGES, MISSING_CREDENTIALS).asText();
        assertTrue(res.asString().contains(expectedMsg));

        ReusableMethod.logTestEnd(testName);
    }

    @Test
    @Tag("test3")
    @Story("Login Fail - Invalid Username Type")
    @Description("Test verifies login with integer username returns 400")
    void username_integer_should_return_400() {
        String testName = "Test3 - Invalid Username Type";
        ReusableMethod.logTestStart(testName);

        Map<String, Object> user = TestDataManager.getDataAsMap(DUMMYJSON, LOGIN, INVALID_USERNAME_TYPE);

        ReusableMethod.logRequest(LOGIN_CREDENTIALS_TXT, user);

        Response res = api.login(user);
        ReusableMethod.logResponse(res);

        assertEquals(HttpStatus.SC_BAD_REQUEST, res.statusCode());

        String expectedMsg = (String) TestDataManager.getDataNode(DUMMYJSON, EXPECTED_MESSAGES, INVALID_USERNAME).asText();
        assertTrue(res.asString().contains(expectedMsg));

        ReusableMethod.logTestEnd(testName);
    }

    @Test
    @Tag("test5")
    @Story("Login Fail - Missing Username")
    @Description("Test verifies login without username returns 400")
    void missing_username_should_return_400() {
        String testName = "Test5 - Missing Username";
        ReusableMethod.logTestStart(testName);

        Map<String, Object> user = TestDataManager.getDataAsMap(DUMMYJSON, LOGIN, MISSING_USERNAME);

        ReusableMethod.logRequest(LOGIN_CREDENTIALS_TXT, user);

        Response res = api.login(user);
        ReusableMethod.logResponse(res);

        assertEquals(HttpStatus.SC_BAD_REQUEST, res.statusCode());

        ReusableMethod.logTestEnd(testName);
    }

    @Test
    @Tag("test7")
    @Story("Login Fail - Empty Body")
    @Description("Test verifies login with empty body returns proper error message")
    void empty_body_should_contain_expected_error_message() {
        String testName = "Test7 - Empty Body Error";
        ReusableMethod.logTestStart(testName);

        Map<String, Object> user = TestDataManager.getDataAsMap(DUMMYJSON, LOGIN, EMPTY_BODY);

        ReusableMethod.logRequest(LOGIN_CREDENTIALS_TXT, user);

        Response res = api.login(user);
        ReusableMethod.logResponse(res);

        assertEquals(HttpStatus.SC_BAD_REQUEST, res.statusCode());
        String msg = res.jsonPath().getString("message");
        assertNotNull(msg);

        String expectedMsg = TestDataManager.getDataNode(DUMMYJSON, EXPECTED_MESSAGES, MISSING_CREDENTIALS).asText();
        assertTrue(msg.contains(expectedMsg));

        ReusableMethod.logTestEnd(testName);
    }

    @Test
    @Tag("test15")
    @Tag("auth")
    @Story("Login Success - Base URL from Config")
    @Description("Test verifies login works using framework baseUrl from config")
    void login_using_framework_baseUrl_should_work() {
        String testName = "Test15 - BaseUrl from Config";
        ReusableMethod.logTestStart(testName);

        Map<String, Object> user = TestDataManager.getDataAsMap(DUMMYJSON, LOGIN, VALID_USER);

        ReusableMethod.logRequest(LOGIN_CREDENTIALS_TXT, user);

        Response res = api.login(user);
        ReusableMethod.logResponse(res);

        assertEquals(HttpStatus.SC_OK, res.statusCode());

        ReusableMethod.logTestEnd(testName);
    }
}