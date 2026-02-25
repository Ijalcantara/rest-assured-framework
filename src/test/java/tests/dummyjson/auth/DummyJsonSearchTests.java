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

import static io.qameta.allure.Allure.step;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Epic("DummyJson API")
@Feature("Search")
@DisplayName("DummyJsonSearchTests")
public class DummyJsonSearchTests extends BaseApiTest {

    private static final Logger log = LoggerUtils.getLogger(DummyJsonSearchTests.class);
    private final DummyJsonClient api = new DummyJsonClient();

    @Story("Positive Scenarios")
    @Test
    @Tag("test11")
    @DisplayName("TC11 - Search users by valid query should return results")
    void users_search_should_return_users_total_limit() {

        step("API Request / Response", () -> {
            Map<String, Object> search = TestDataManager.getDataAsMap("dummyjson", "search");
            String query = (String) search.get("query");
            log.info("Search query: {}", query);

            Allure.addAttachment("Search Query", query);
            Response res = api.searchUsers(query);
            ReusableMethod.attachJsonResponse("Response Body", res);

            String status = res.statusCode() + " " + res.statusLine().split(" ", 3)[2];
            Allure.addAttachment("Status Code", status);
            Assertions.assertEquals(200, res.statusCode(), "Expected HTTP 200 OK");
        });
    }
}