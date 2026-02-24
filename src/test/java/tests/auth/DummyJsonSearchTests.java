package tests.auth;

import clients.DummyJsonClient;
import core.BaseApiTest;
import core.TestDataManager;
import io.qameta.allure.*;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import utils.LoggerUtils;
import utils.reusablemethod.ReusableMethod;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@Epic("DummyJson API")
@Feature("Search")
@DisplayName("DummyJsonSearchTests")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class DummyJsonSearchTests extends BaseApiTest {

    private static final Logger log = LoggerUtils.getLogger(DummyJsonSearchTests.class);
    private final DummyJsonClient api = new DummyJsonClient();

    @Test
    @Tag("test11")
    @Story("Search Users by Query")
    @Description("Test verifies that searching users by query returns correct total and limit")
    void users_search_should_return_users_total_limit() {

        String testName = "Test11 - Users Search";

        Allure.step("Start test: " + testName);
        ReusableMethod.logTestStart(testName);

        // Step 1: Get test data
        Map<String, Object> search = Allure.step("Get search data from TestDataManager",
                () -> TestDataManager.getDataAsMap("dummyjson", "search"));

        String query = (String) search.get("query");
        log.info("Search query: {}", query);
        Allure.attachment("Search Query", query);

        // Step 2: Send request
        Response res = Allure.step("Send search users API request",
                () -> api.searchUsers(query));

        // Step 3: Log + Attach response
        ReusableMethod.logResponse(res);
        Allure.attachment("Response Body", res.asString());
        Allure.attachment("Status Code", String.valueOf(res.statusCode()));

        // Step 4: Validate status
        Allure.step("Validate HTTP status is 200", () ->
                assertEquals(HttpStatus.SC_OK, res.statusCode(), "Expected HTTP 200")
        );

        // Step 5: Extract response values
        List<?> users = res.jsonPath().getList("users");
        Integer total = res.jsonPath().getInt("total");
        Integer limit = res.jsonPath().getInt("limit");

        log.info("Users returned: {}", users != null ? users.size() : 0);
        log.info("Total: {}", total);
        log.info("Limit: {}", limit);

        // Step 6: Validate response body
        Allure.step("Validate users list is not null", () ->
                assertNotNull(users, "Users list should not be null")
        );

        Allure.step("Validate total is not null", () ->
                assertNotNull(total, "Total should not be null")
        );

        Allure.step("Validate limit is not null", () ->
                assertNotNull(limit, "Limit should not be null")
        );

        ReusableMethod.logTestEnd(testName);
        Allure.step("End test: " + testName);
    }
}