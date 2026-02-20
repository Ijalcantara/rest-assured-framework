package tests.auth;

import clients.DummyJsonClient;
import core.BaseApiTest;
import core.RequestSpecFactory;
import core.TestDataManager;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.slf4j.Logger;
import utils.LoggerUtils;
import utils.TokenHelper;
import utils.reusablemethod.ReusableMethod;

import static org.junit.jupiter.api.Assertions.*;

@Epic("DummyJson API")
@Feature("User Me Endpoint")
@DisplayName("DummyJsonUserMeTests")
public class DummyJsonUserMeTests extends BaseApiTest {

    private static final Logger log = LoggerUtils.getLogger(DummyJsonUserMeTests.class);
    private final DummyJsonClient api = new DummyJsonClient();

    @Test
    @Tag("test8")
    @Story("Get user info with valid token")
    @Description("Test verifies that /user/me returns 200 and correct user info with a valid token")
    void Test8_user_me_valid_token_should_return_200_and_user_info() {
        String testName = "Test8 - Valid Token";
        ReusableMethod.logTestStart(testName);

        String token = TokenHelper.getValidUserToken();
        Response res = api.userMe(token);

        ReusableMethod.logResponse(res);

        assertEquals(HttpStatus.SC_OK, res.statusCode());
        assertNotNull(res.jsonPath().get("id"));
        assertNotNull(res.jsonPath().get("username"));

        ReusableMethod.logTestEnd(testName);
    }

    @Test
    @Tag("auth")
    @Story("Get user info with invalid token")
    @Description("Test verifies that /user/me returns 401 Unauthorized with an invalid token")
    void Test10_user_me_invalid_token_should_return_401() {
        String testName = "Test10 - Invalid Token";
        ReusableMethod.logTestStart(testName);

        String invalidToken = TestDataManager.getDataNode("dummyjson", "login", "invalidToken").asText();
        Response res = api.userMe(invalidToken);

        ReusableMethod.logResponse(res);

        assertEquals(HttpStatus.SC_UNAUTHORIZED, res.statusCode(),
                "Expected 401 for invalid token but got " + res.statusCode() + ". Body: " + res.asString());

        ReusableMethod.logTestEnd(testName);
    }

    @Test
    @Tag("test12")
    @Story("User info remains consistent across multiple calls")
    @Description("Test verifies that multiple calls to /user/me with the same valid token return the same user data")
    void Test12_user_me_multiple_times_should_return_same_user() {
        String testName = "Test12 - Multiple Calls";
        ReusableMethod.logTestStart(testName);

        String token = TokenHelper.getValidUserToken();

        Response first = api.userMe(token);
        ReusableMethod.logResponse(first);

        assertEquals(HttpStatus.SC_OK, first.statusCode());
        int id1 = first.jsonPath().getInt("id");
        String username1 = first.jsonPath().getString("username");

        for (int i = 0; i < 7; i++) {
            Response next = api.userMe(token);
            ReusableMethod.logResponse(next);

            assertEquals(HttpStatus.SC_OK, next.statusCode());
            assertEquals(id1, next.jsonPath().getInt("id"));
            assertEquals(username1, next.jsonPath().getString("username"));
        }

        log.info("User data consistent across multiple calls.");
        ReusableMethod.logTestEnd(testName);
    }

    @Test
    @Tag("test13")
    @Story("Get user info without Accept-Encoding header")
    @Description("Test verifies that /user/me still returns 200 even without Accept-Encoding header")
    void Test13_user_me_without_accept_encoding_should_still_return_200() {
        String testName = "Test13 - Without Accept-Encoding";
        ReusableMethod.logTestStart(testName);

        String token = TokenHelper.getValidUserToken();

        Response res = io.restassured.RestAssured.given()
                .spec(RequestSpecFactory.dummyJson())
                .header("Authorization", "Bearer " + token)
                .header("Accept-Encoding", "identity")
                .when()
                .get("/user/me");

        ReusableMethod.logResponse(res);
        assertEquals(HttpStatus.SC_OK, res.statusCode());

        ReusableMethod.logTestEnd(testName);
    }

    @Test
    @Tag("test9")
    @Story("Get user info with expired token")
    @Description("Test verifies that /user/me returns 401 Unauthorized when token is expired")
    void Test9_user_me_expired_token_should_return_401() {
        String testName = "Test9 - Expired Token";
        ReusableMethod.logTestStart(testName);

        String expiredToken = TestDataManager.getDataNode("dummyjson", "login", "expiredToken").asText();
        Response res = api.userMe(expiredToken);

        ReusableMethod.logResponse(res);

        assertEquals(HttpStatus.SC_UNAUTHORIZED, res.statusCode(),
                "Expected 401 for expired token but got " + res.statusCode() + ". Body: " + res.asString());

        ReusableMethod.logTestEnd(testName);
    }
}