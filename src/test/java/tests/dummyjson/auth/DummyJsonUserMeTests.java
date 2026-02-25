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
        log.info("TC08: Calling /user/me with token={}", token);

        Response res = api.userMe(token);

        log.info("TC08: Response status={} body={}", res.statusCode(), res.getBody().asString());
        ReusableMethod.attachApiCall(Map.of("token", token), res);

        assertEquals(HttpStatus.SC_OK, res.statusCode(), "Expected HTTP 200 OK");
    }

    @Story("Positive Scenarios")
    @Test
    @Tag("test12")
    @DisplayName("TC12 - Multiple calls return same user info")
    void user_me_multiple_times_should_return_same_user() {
        String token = TokenHelper.getValidUserToken();
        log.info("TC12: Calling /user/me multiple times with token={}", token);

        Allure.step("API Request / Response for multiple calls", () -> {
            Response first = api.userMe(token);
            log.info("TC12 - Call 1: status={} body={}", first.statusCode(), first.getBody().asString());
            ReusableMethod.attachApiCall(Map.of("token", token), first);

            assertEquals(HttpStatus.SC_OK, first.statusCode(), "Expected HTTP 200 OK");

            int expectedId = first.jsonPath().getInt("id");
            String expectedUsername = first.jsonPath().getString("username");

            for (int i = 2; i <= 8; i++) {
                Response next = api.userMe(token);
                log.info("TC12 - Call {}: status={} body={}", i, next.statusCode(), next.getBody().asString());
                ReusableMethod.attachApiCall(Map.of("token", token), next);

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
        log.info("TC13: Calling /user/me without Accept-Encoding header with token={}", token);

        Response res = io.restassured.RestAssured.given()
                .spec(RequestSpecFactory.dummyJson())
                .header("Authorization", "Bearer " + token)
                .header("Accept-Encoding", "identity")
                .log().all() // logs request
                .when()
                .get("/user/me")
                .then()
                .log().all() // logs response
                .extract()
                .response();

        ReusableMethod.attachApiCall(Map.of("token", token), res);
        assertEquals(HttpStatus.SC_OK, res.statusCode(), "Expected HTTP 200 OK");
    }

    @Story("Negative Scenarios")
    @Test
    @Tag("test9")
    @DisplayName("TC09 - Get user info with expired token")
    void user_me_expired_token_should_return_401() {
        String expiredToken = TestDataManager.getDataNode("dummyjson", "login", "expiredToken").asText();
        log.info("TC09: Calling /user/me with expired token={}", expiredToken);

        Response res = api.userMe(expiredToken);

        log.info("TC09: Response status={} body={}", res.statusCode(), res.getBody().asString());
        ReusableMethod.attachApiCall(Map.of("token", expiredToken), res);

        assertEquals(HttpStatus.SC_UNAUTHORIZED, res.statusCode(), "Expected HTTP 401 Unauthorized");
    }

    @Story("Negative Scenarios")
    @Test
    @Tag("test10")
    @DisplayName("TC10 - Get user info with invalid token")
    void user_me_invalid_token_should_return_401() {
        String invalidToken = TestDataManager.getDataNode("dummyjson", "login", "invalidToken").asText();
        log.info("TC10: Calling /user/me with invalid token={}", invalidToken);

        Response res = api.userMe(invalidToken);

        log.info("TC10: Response status={} body={}", res.statusCode(), res.getBody().asString());
        ReusableMethod.attachApiCall(Map.of("token", invalidToken), res);

        assertEquals(HttpStatus.SC_UNAUTHORIZED, res.statusCode(), "Expected HTTP 401 Unauthorized");
    }
}