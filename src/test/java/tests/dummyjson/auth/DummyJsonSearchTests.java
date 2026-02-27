package tests.dummyjson.auth;

import clients.DummyJsonClient;
import constant.ConstantClass;
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

        Map<String, Object> search =
                TestDataManager.getDataAsMap(ConstantClass.DUMMYJSON, ConstantClass.SEARCH);

        String query = (String) search.get(ConstantClass.FIELD_QUERY);
        log.info("Search query: {}", query);

        Map<String, Object> requestPayload = Map.of(ConstantClass.FIELD_QUERY, query);
        Response res = api.searchUsers(query);

        ReusableMethod.validateApiScenario(
                "User searches for users with a valid query string: '" + query + "'.",
                requestPayload,
                res,
                200,
                "total", "limit", "users"
        );

        ReusableMethod.attachApiCall(requestPayload, res);
    }

}