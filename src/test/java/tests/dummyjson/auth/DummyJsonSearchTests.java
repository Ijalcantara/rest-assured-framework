package tests.dummyjson.auth;

import clients.DummyJsonClient;
import constant.ConstantClass;
import core.BaseApiTest;
import io.qameta.allure.*;
import io.restassured.response.Response;
import manager.TestDataManager;
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.AuthTokenUtil;
import utils.ApiAllureUtil;
import utils.ApiTestUtils;

import java.util.Map;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Epic("DummyJson API")
@Feature("Search")
@DisplayName("DummyJsonSearchTests")
@Tag("dummyjson")
public class DummyJsonSearchTests extends BaseApiTest {

    private final DummyJsonClient api = new DummyJsonClient();
    private static final Logger log = LoggerFactory.getLogger(BaseApiTest.class);

    @Test
    @Tag("test11")
    @DisplayName("TC11 - Search users by valid query should return results")
    void users_search_should_return_users_total_limit() {

        Map<String, Object> search =
                TestDataManager.getDataAsMap(ConstantClass.DUMMYJSON, ConstantClass.SEARCH);

        String query = (String) search.get(ConstantClass.FIELD_QUERY);

        Map<String, Object> loginPayload = TestDataManager.getNestedDataAsMap(
                ConstantClass.DUMMYJSON,
                ConstantClass.LOGIN,
                ConstantClass.VALID_USER
        );

        Response loginResponse = api.login(loginPayload);

        // Extract token safely
        String token = AuthTokenUtil.getToken(loginResponse, "accessToken");
        log.info("Login response: " + loginResponse.asPrettyString());

        // Make the API call
        Response res = api.searchUsers(token, query);

        // Check if blocked by Cloudflare / rate limit
        ApiTestUtils.assumeNotCloudflare(res);

        if (res.getStatusCode() == 429) {
            log.warn("API rate limit hit (429). Skipping test to avoid failures.");
            Assumptions.assumeTrue(false, "Skipping due to rate limit");
        }

        // Normal assertions
        ApiAllureUtil.logScenario("User searches for users with query: '" + query + "'.");
        ApiAllureUtil.validateStatusCode(res, 200);
        ApiAllureUtil.validateResponseBody(res, "total", "limit", "users");
        ApiAllureUtil.attachApiCall(Map.of("query", query), res);
    }
}