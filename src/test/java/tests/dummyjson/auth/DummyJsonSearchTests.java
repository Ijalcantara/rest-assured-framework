package tests.dummyjson.auth;

import clients.DummyJsonClient;
import core.BaseApiTest;
import core.TestDataManager;
import io.qameta.allure.*;
import io.restassured.response.Response;
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import utils.LoggerUtils;
import utils.reusablemethod.ReusableMethod;

import java.util.Map;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Epic("DummyJson API")
@Feature("Search")
@DisplayName("DummyJsonSearchTests")
public class DummyJsonSearchTests extends BaseApiTest {

    private static final Logger log = LoggerUtils.getLogger(DummyJsonSearchTests.class);
    private final DummyJsonClient api = new DummyJsonClient();

    @Test
    @Tag("test11")
    @DisplayName("TC11 - Search users by valid query should return results")
    void users_search_should_return_users_total_limit() {

        Map<String, Object> search = TestDataManager.getDataAsMap("dummyjson", "search");
        String query = (String) search.get("query");
        log.info("Search query: {}", query);

        Map<String, Object> requestPayload = Map.of("query", query);
        Response res = api.searchUsers(query);

        // Attach request & response in one Allure step
        ReusableMethod.attachApiCall(requestPayload, res);

        Assertions.assertEquals(200, res.statusCode(), "Expected HTTP 200 OK");
    }
}