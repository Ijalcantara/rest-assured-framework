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

    @Test
    @Tag("TC01")
    @DisplayName("TC01 - Create a new user and verify via GET")
    void create_then_get_user_should_match() {

        // ===== Sanity check =====
        log.info("Step: Checking /users endpoint to ensure API is reachable");
        Response sanity = api.listUsers();
        assumeNotCloudflare(sanity);

        ReusableMethod.validateApiScenario(
                "Check /users endpoint to ensure API is reachable.",
                Map.of("endpoint", "/users"),
                sanity,
                HttpStatus.SC_OK
        );
        ReusableMethod.attachApiCall(Map.of("endpoint", "/users"), sanity);
        log.info("Sanity check status: {}", sanity.statusCode());

        // ===== Prepare new user =====
        String unique = UUID.randomUUID().toString();
        String email = "user_" + unique + "@example.com";
        String name = "Automation User " + unique;

        Map<String, Object> userPayload = new HashMap<>();
        userPayload.put(ConstantClass.FIELD_NAME, name);
        userPayload.put(ConstantClass.GOREST_FIELD_EMAIL, email);
        userPayload.put(ConstantClass.FIELD_GENDER, "male");
        userPayload.put(ConstantClass.FIELD_STATUS, "active");
        log.info("Prepared new user payload: {}", userPayload);

        // ===== CREATE USER =====
        log.info("Step: Creating a new user");
        Response createRes = api.createUser(userPayload);
        assumeNotCloudflare(createRes);

        ReusableMethod.validateApiScenario(
                "Create a new user with unique email and name.",
                userPayload,
                createRes,
                HttpStatus.SC_CREATED,
                ConstantClass.GOREST_FIELD_ID,
                ConstantClass.FIELD_NAME,
                ConstantClass.GOREST_FIELD_EMAIL
        );
        ReusableMethod.attachApiCall(userPayload, createRes);
        log.info("Create response status: {}, body: {}", createRes.statusCode(), createRes.asString());

        Integer id = createRes.jsonPath().getInt(ConstantClass.GOREST_FIELD_ID);
        assertNotNull(id, "Create response should include id");
        log.info("Created user ID: {}", id);

        // ===== GET USER =====
        log.info("Step: Retrieving the user with ID {}", id);
        Response getRes = api.getUser(id);
        assumeNotCloudflare(getRes);

        ReusableMethod.validateApiScenario(
                "Retrieve the user created via GET /users/{id}.",
                Map.of(ConstantClass.GOREST_FIELD_USER_ID, id),
                getRes,
                HttpStatus.SC_OK,
                ConstantClass.GOREST_FIELD_ID,
                ConstantClass.FIELD_NAME,
                ConstantClass.GOREST_FIELD_EMAIL
        );
        ReusableMethod.attachApiCall(Map.of(ConstantClass.GOREST_FIELD_USER_ID, id), getRes);
        log.info("Get response status: {}, body: {}", getRes.statusCode(), getRes.asString());

        // ===== Assertions =====
        Map<String, Object> returnedUser = getRes.jsonPath().getMap("");
        assertNotNull(returnedUser, "Returned user should not be null");

        assertEquals(email, returnedUser.get(ConstantClass.GOREST_FIELD_EMAIL));
        assertEquals(name, returnedUser.get(ConstantClass.FIELD_NAME));
        assertEquals("male", returnedUser.get(ConstantClass.FIELD_GENDER));
        assertEquals("active", returnedUser.get(ConstantClass.FIELD_STATUS));
        log.info("User data verified successfully for ID {}", id);
    }

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
}