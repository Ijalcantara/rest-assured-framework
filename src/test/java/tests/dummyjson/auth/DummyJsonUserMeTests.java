package tests.dummyjson.auth;

import clients.DummyJsonClient;
import core.BaseApiTest;
import core.RequestSpecFactory;
import core.TestDataManager;
import io.qameta.allure.*;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import utils.LoggerUtils;
import utils.TokenHelper;
import utils.reusablemethod.ReusableMethod;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@Epic("DummyJson API")
@Feature("User Me Endpoint")
@DisplayName("DummyJsonUserMeTests")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class DummyJsonUserMeTests extends BaseApiTest {

    private static final Logger log = LoggerUtils.getLogger(DummyJsonUserMeTests.class);
    private final DummyJsonClient api = new DummyJsonClient();

    @Story("Positive Scenarios")
    @Test
    @Tag("test8")
    @DisplayName("TC08 - Get user info with valid token")
    void user_me_valid_token_should_return_200() {
        String token = TokenHelper.getValidUserToken();
        Response res = api.userMe(token);

        ReusableMethod.attachJsonResponse("Response Body", res);
        assertEquals(HttpStatus.SC_OK, res.statusCode(), "Expected HTTP 200 OK");
    }

    @Story("Positive Scenarios")
    @Test
    @Tag("test12")
    @DisplayName("TC12 - Multiple calls return same user info")
    void user_me_multiple_times_should_return_same_user() {
        String token = TokenHelper.getValidUserToken();
        Allure.step("API Request / Response", () -> {

            Response first = api.userMe(token);
            ReusableMethod.attachJsonResponse("Response Body - First Call", first);
            assertEquals(HttpStatus.SC_OK, first.statusCode(), "Expected HTTP 200 OK");

            int expectedId = first.jsonPath().getInt("id");
            String expectedUsername = first.jsonPath().getString("username");

            for (int i = 2; i <= 8; i++) {
                Response next = api.userMe(token);
                ReusableMethod.attachJsonResponse("Response Body - Call " + i, next);
                assertEquals(HttpStatus.SC_OK, next.statusCode(), "Expected HTTP 200 OK");
                assertEquals(expectedId, next.jsonPath().getInt("id"), "ID should remain consistent");
                assertEquals(expectedUsername, next.jsonPath().getString("username"), "Username should remain consistent");
            }
        });
    }

    @Story("Positive Scenarios")
    @Test
    @Tag("test13")
    @DisplayName("TC13 - Get user info without Accept-Encoding header")
    void user_me_without_accept_encoding_should_still_return_200() {
        String token = TokenHelper.getValidUserToken();
        Allure.step("API Request / Response", () -> {
            Response res = io.restassured.RestAssured.given()
                    .spec(RequestSpecFactory.dummyJson())
                    .header("Authorization", "Bearer " + token)
                    .header("Accept-Encoding", "identity")
                    .when()
                    .get("/user/me");

            ReusableMethod.attachJsonResponse("Response Body", res);
            assertEquals(HttpStatus.SC_OK, res.statusCode(), "Expected HTTP 200 OK");
        });
    }

    @Story("Negative Scenarios")
    @Test
    @Tag("test9")
    @DisplayName("TC09 - Get user info with expired token")
    void user_me_expired_token_should_return_401() {
        String expiredToken = TestDataManager.getDataNode("dummyjson", "login", "expiredToken").asText();
        Map<String, Object> requestPayload = Map.of("token", expiredToken);

        Allure.step("API Request / Response", () -> {
            Allure.addAttachment("Request Payload", requestPayload.toString());
            Response res = api.userMe(expiredToken);
            ReusableMethod.attachJsonResponse("Response Body", res);

            String status = res.statusCode() + " " + res.statusLine().split(" ", 3)[2];
            Allure.addAttachment("Status Code", status);
            assertEquals(HttpStatus.SC_UNAUTHORIZED, res.statusCode(), "Expected HTTP 401 Unauthorized");
        });
    }

    @Story("Negative Scenarios")
    @Test
    @Tag("test10")
    @DisplayName("TC10 - Get user info with invalid token")
    void user_me_invalid_token_should_return_401() {
        String invalidToken = TestDataManager.getDataNode("dummyjson", "login", "invalidToken").asText();
        Map<String, Object> requestPayload = Map.of("token", invalidToken);

        Allure.step("API Request / Response", () -> {
            Allure.addAttachment("Request Payload", requestPayload.toString());
            Response res = api.userMe(invalidToken);
            ReusableMethod.attachJsonResponse("Response Body", res);

            String status = res.statusCode() + " " + res.statusLine().split(" ", 3)[2];
            Allure.addAttachment("Status Code", status);
            assertEquals(HttpStatus.SC_UNAUTHORIZED, res.statusCode(), "Expected HTTP 401 Unauthorized");
        });
    }
}