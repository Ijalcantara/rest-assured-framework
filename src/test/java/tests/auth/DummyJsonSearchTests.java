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
        ReusableMethod.logTestStart(testName);

        // Get search query directly from TestDataManager
        Map<String, Object> search = TestDataManager.getDataAsMap("dummyjson", "search");
        String query = (String) search.get("query");
        log.info("Search query: {}", query);

        // Call API
        Response res = api.searchUsers(query);

        // Log response
        ReusableMethod.logResponse(res);

        // Assertions
        assertEquals(HttpStatus.SC_OK, res.statusCode(), "Expected HTTP status 200");

        List<?> users = res.jsonPath().getList("users");
        Integer total = res.jsonPath().getInt("total");
        Integer limit = res.jsonPath().getInt("limit");

        log.info("Users returned: {}", users != null ? users.size() : 0);
        log.info("Total: {}", total);
        log.info("Limit: {}", limit);

        assertNotNull(users, "Users list should not be null");
        assertNotNull(total, "Total should not be null");
        assertNotNull(limit, "Limit should not be null");

        ReusableMethod.logTestEnd(testName);
    }
}