package tests.integration;

import clients.GoRestClient;
import core.BaseApiTest;
import io.qameta.allure.*;
import io.restassured.response.Response;
import manager.TestDataManager;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.ApiAllureUtil;
import utils.ApiTestMethods; // ✅ import the utility class

import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@Epic("GoRest API")
@Feature("User CRUD Operations")
@DisplayName("GoRestUserCrudTests")
@Tag("gorest")
public class GoRestUserCrudTests extends BaseApiTest {

    private static final Logger log = LoggerFactory.getLogger(GoRestUserCrudTests.class);
    private static final String TOKEN = ApiTestMethods.resolveToken();
    private final GoRestClient api = new GoRestClient(TOKEN);

    @Test
    @Tag("TC01")
    @DisplayName("TC01 - Create a new user and verify via GET")
    @Story("Create and GET user")
    void create_then_get_user_should_match() {

        Response sanity = api.listUsers();
        ApiAllureUtil.attachApiCall(Map.of("endpoint", "/users"), sanity);

        ApiTestMethods.assumeNotCloudflare(sanity);

        Map<String, Object> userPayload = TestDataManager.getDataAsMap("gorest", "createUser");

        String unique = UUID.randomUUID().toString();
        userPayload.put("name", userPayload.get("name") + " " + unique);
        userPayload.put("email", "user_" + unique + "@example.com");

        Response createRes = api.createUser(userPayload);
        ApiAllureUtil.attachApiCall(userPayload, createRes);
        ApiTestMethods.assumeNotCloudflare(createRes);

        ApiAllureUtil.validateStatusCode(createRes, HttpStatus.SC_CREATED);
        Integer id = createRes.jsonPath().getInt("id");
        assertNotNull(id, "Create response should include id");

        Response getRes = api.getUser(id);
        ApiAllureUtil.attachApiCall(Map.of("userId", id), getRes);
        ApiTestMethods.assumeNotCloudflare(getRes);

        ApiAllureUtil.validateStatusCode(getRes, HttpStatus.SC_OK);
        Map<String, Object> returnedUser = getRes.jsonPath().getMap("");
        assertNotNull(returnedUser);

        assertEquals(userPayload.get("email"), returnedUser.get("email"));
        assertEquals(userPayload.get("name"), returnedUser.get("name"));
        assertEquals(userPayload.get("gender"), returnedUser.get("gender"));
        assertEquals(userPayload.get("status"), returnedUser.get("status"));
    }
}