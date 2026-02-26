package tests.integration;

import clients.GoRestClient;
import config.ConfigManager;
import constant.ConstantClass;
import core.BaseApiTest;
import io.qameta.allure.*;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.reusablemethod.ReusableMethod;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@Epic("GoRest API")
@Feature("User CRUD Operations")
@DisplayName("GoRestUserCrudTests")
@Tag("gorest")
public class GoRestUserCrudTests extends BaseApiTest {

    private static final Logger log = LoggerFactory.getLogger(GoRestUserCrudTests.class);

    private static final String TOKEN = resolveToken();
    private final GoRestClient api = new GoRestClient(TOKEN);

    private static String resolveToken() {
        String env = System.getenv("GOREST_TOKEN");
        if (env != null && !env.isBlank()) return env.trim();

        String cfg = ConfigManager.get("gorest.token");
        if (cfg != null && !cfg.isBlank()) return cfg.trim();

        throw new IllegalStateException("GoRest token not found. Set GOREST_TOKEN or config gorest.token.");
    }

    private static boolean looksLikeCloudflare(Response res) {
        String ct = res.getHeader("content-type");
        String body = res.asString();
        if (ct == null) ct = "";
        ct = ct.toLowerCase();
        return ct.contains("text/html")
                && body.contains("Just a moment")
                && (body.contains("challenge-platform") || body.contains("_cf_chl_opt"));
    }

    private static void assumeNotCloudflare(Response res) {
        Assumptions.assumeFalse(
                looksLikeCloudflare(res),
                "Blocked by Cloudflare challenge. Exclude @Tag(\"gorest\") in CI or use a self-hosted runner."
        );
    }

    @Test
    @Tag("TC01")
    @DisplayName("TC01 - Create a new user and verify via GET")
    @Story("Create and GET user")
    void create_then_get_user_should_match() {

        // Preflight sanity
        Response sanity = api.listUsers();
        Allure.step("API Sanity (List Users)", () -> {
            ReusableMethod.attachApiCall(Map.of("endpoint", "/users"), sanity);
            ReusableMethod.attachBusinessSummary(
                    "Check /users endpoint to ensure API is reachable.",
                    "System should return 200 OK or 401 Unauthorized depending on token validity.",
                    sanity
            );
        });
        assumeNotCloudflare(sanity);
        assertTrue(
                sanity.statusCode() == HttpStatus.SC_OK || sanity.statusCode() == HttpStatus.SC_UNAUTHORIZED,
                () -> "Unexpected sanity status: " + sanity.statusCode() + " body=" + sanity.asString()
        );

        String unique = UUID.randomUUID().toString();
        String email = "user_" + unique + "@example.com";
        String name = "Automation User " + unique;

        Map<String, Object> userPayload = new HashMap<>();
        userPayload.put(ConstantClass.FIELD_NAME, name);
        userPayload.put(ConstantClass.GOREST_FIELD_EMAIL, email);
        userPayload.put(ConstantClass.FIELD_GENDER, "male");
        userPayload.put(ConstantClass.FIELD_STATUS, "active");

        // CREATE
        Response createRes = api.createUser(userPayload);
        Allure.step("API Request / Response (Create)", () -> {
            ReusableMethod.attachApiCall(userPayload, createRes);
            ReusableMethod.attachBusinessSummary(
                    "Create a new user with unique email and name.",
                    "System should accept the request and return 201 Created with user ID.",
                    createRes
            );
        });
        assumeNotCloudflare(createRes);

        assertEquals(HttpStatus.SC_CREATED, createRes.statusCode(),
                () -> "Expected 201 CREATED, got " + createRes.statusCode() + " body=" + createRes.asString());

        Integer id = createRes.jsonPath().getInt(ConstantClass.GOREST_FIELD_ID);
        assertNotNull(id, "Create response should include id");

        // GET
        Response getRes = api.getUser(id);
        Allure.step("API Request / Response (Get)", () -> {
            ReusableMethod.attachApiCall(Map.of(ConstantClass.GOREST_FIELD_USER_ID, id), getRes);
            ReusableMethod.attachBusinessSummary(
                    "Retrieve the user created via GET /users/{id}.",
                    "System should return 200 OK and all user details match what was created.",
                    getRes
            );
        });
        assumeNotCloudflare(getRes);

        assertEquals(HttpStatus.SC_OK, getRes.statusCode(),
                () -> "Expected 200 OK, got " + getRes.statusCode() + " body=" + getRes.asString());

        Map<String, Object> returnedUser = getRes.jsonPath().getMap("");
        assertNotNull(returnedUser);

        assertEquals(email, returnedUser.get(ConstantClass.GOREST_FIELD_EMAIL));
        assertEquals(name, returnedUser.get(ConstantClass.FIELD_NAME));
        assertEquals("male", returnedUser.get(ConstantClass.FIELD_GENDER));
        assertEquals("active", returnedUser.get(ConstantClass.FIELD_STATUS));
    }
}