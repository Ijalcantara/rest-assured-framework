package tests.dummyjson.auth;

import clients.DummyJsonClient;
import core.BaseApiTest;
import core.TestDataManager;
import io.qameta.allure.*;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.*;
import utils.reusablemethod.ReusableMethod;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Epic("DummyJson API")
@Feature("DummyJsonAuthTests")
@DisplayName("DummyJsonAuthTests")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class DummyJsonAuthTests extends BaseApiTest {

    private final DummyJsonClient api = new DummyJsonClient();

    @Story("Positive Scenarios")
    @Test
    @Tag("auth")
    @DisplayName("TC01 - Login with valid credentials should return 200")
    void login_success_should_return_200() {
        Map<String, Object> requestPayload = TestDataManager.getDataAsMap("dummyjson", "login", "validUser");

        Response res = api.login(requestPayload);
        ReusableMethod.attachApiCall(requestPayload, res);

        assertEquals(HttpStatus.SC_OK, res.statusCode(), "Expected HTTP 200 OK");
        String password = res.jsonPath().getString("password");
        assert password == null : "Password should not be returned in response";
    }

    @Story("Positive Scenarios")
    @Test
    @Tag("auth")
    @DisplayName("TC02 - Login using framework baseUrl should work")
    void login_using_framework_baseUrl_should_work() {
        Map<String, Object> requestPayload = TestDataManager.getDataAsMap("dummyjson", "login", "validUser");

        Response res = api.login(requestPayload);
        ReusableMethod.attachApiCall(requestPayload, res);

        assertEquals(HttpStatus.SC_OK, res.statusCode(), "Expected HTTP 200 OK");
        String password = res.jsonPath().getString("password");
        assert password == null : "Password should not be returned in response";
    }

    @Story("Negative Scenarios")
    @Test
    @Tag("auth")
    @DisplayName("TC03 - Missing password should return 400")
    void missing_password_should_return_400() {
        Map<String, Object> requestPayload = TestDataManager.getDataAsMap("dummyjson", "login", "missingPassword");

        Response res = api.login(requestPayload);
        ReusableMethod.attachApiCall(requestPayload, res);
        assertEquals(HttpStatus.SC_BAD_REQUEST, res.statusCode(), "Expected HTTP 400 Bad Request");
    }

    @Story("Negative Scenarios")
    @Test
    @Tag("auth")
    @DisplayName("TC04 - Missing username should return 400")
    void missing_username_should_return_400() {
        Map<String, Object> requestPayload = TestDataManager.getDataAsMap("dummyjson", "login", "missingUsername");

        Response res = api.login(requestPayload);
        ReusableMethod.attachApiCall(requestPayload, res);
        assertEquals(HttpStatus.SC_BAD_REQUEST, res.statusCode(), "Expected HTTP 400 Bad Request");
    }

    @Story("Negative Scenarios")
    @Test
    @Tag("auth")
    @DisplayName("TC05 - Invalid username type should return 400")
    void username_integer_should_return_400() {
        Map<String, Object> requestPayload = TestDataManager.getDataAsMap("dummyjson", "login", "invalidUsernameType");

        Response res = api.login(requestPayload);
        ReusableMethod.attachApiCall(requestPayload, res);
        assertEquals(HttpStatus.SC_BAD_REQUEST, res.statusCode(), "Expected HTTP 400 Bad Request");
    }

    @Story("Negative Scenarios")
    @Test
    @Tag("auth")
    @DisplayName("TC06 - Empty body should return 400")
    void empty_body_should_return_error() {
        Map<String, Object> requestPayload = TestDataManager.getDataAsMap("dummyjson", "login", "emptyBody");

        Response res = api.login(requestPayload);
        ReusableMethod.attachApiCall(requestPayload, res);
        assertEquals(HttpStatus.SC_BAD_REQUEST, res.statusCode(), "Expected HTTP 400 Bad Request");
    }
}