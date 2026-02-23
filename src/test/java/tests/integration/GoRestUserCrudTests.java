package tests.integration;

import core.BaseApiTest;
import clients.GorestClient;
import config.ConfigManager;
import io.qameta.allure.*;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import static io.qameta.allure.Allure.step;
import static org.junit.jupiter.api.Assertions.*;

@Epic("GoRest API")
@Feature("User CRUD Operations")
@DisplayName("GoRestUserCrudTests")
public class GoRestUserCrudTests extends BaseApiTest {

    private static final Logger log = LoggerFactory.getLogger(GoRestUserCrudTests.class);

    // ✅ Read token from environment variable first, fallback to config file
    private static final String TOKEN =
            System.getenv("GOREST_TOKEN") != null
                    ? System.getenv("GOREST_TOKEN")
                    : ConfigManager.get("gorest.token");

    static {
        if (TOKEN == null || TOKEN.isEmpty()) {
            throw new IllegalStateException(
                    "GOREST_TOKEN is not set! " +
                            "Set it in CI/CD secrets or in dev/qa properties."
            );
        }
    }

    private final GorestClient api = new GorestClient(TOKEN);

    @Test
    @Tag("gorest")
    @Story("Create a new user and verify via GET")
    @Description("Test verifies that creating a new GoRest user and fetching it by ID returns correct data")
    void Test14_create_then_get_user_should_match() {

        step("Start Test14 - GoRest Create & Verify", () -> log.info("========== START Test14 =========="));

        // Dynamic user data
        String unique = String.valueOf(System.currentTimeMillis());
        String email = "user" + unique + "@example.com";
        String name = "Automation User " + unique;

        Map<String, Object> userPayload = new HashMap<>();
        userPayload.put("name", name);
        userPayload.put("email", email);
        userPayload.put("gender", "male");
        userPayload.put("status", "active");

        step("Prepare user payload", () -> log.info("Creating user with email: {}", email));

        // ✅ Use AtomicReference to store Response from lambda
        AtomicReference<Response> createResRef = new AtomicReference<>();
        step("Send POST request to create user", () -> createResRef.set(api.createUser(userPayload)));
        Response createRes = createResRef.get();

        if (createRes.statusCode() == 401 || createRes.statusCode() == 403) {
            log.error("Authentication failed during CREATE! Status: {}. Response: {}", createRes.statusCode(), createRes.asString());
            fail("Authentication failed: Check your GOREST_TOKEN!");
        }

        step("Log create response", () -> log.info("Create Status: {}", createRes.statusCode()));
        assertEquals(HttpStatus.SC_CREATED, createRes.statusCode(), "Expected 201 CREATED");

        Integer id = createRes.jsonPath().getInt("id");
        step("Log created user ID", () -> log.info("Created User ID: {}", id));

        // ✅ GET user
        AtomicReference<Response> getResRef = new AtomicReference<>();
        step("Send GET request to fetch user by ID", () -> getResRef.set(api.getUser(id)));
        Response getRes = getResRef.get();

        if (getRes.statusCode() == 401 || getRes.statusCode() == 403) {
            log.error("Authentication failed during GET! Status: {}. Response: {}", getRes.statusCode(), getRes.asString());
            fail("Authentication failed: Check your GOREST_TOKEN!");
        }

        step("Log get response", () -> log.info("Get Status: {}", getRes.statusCode()));
        assertEquals(HttpStatus.SC_OK, getRes.statusCode(), "Expected 200 OK");

        Map<String, Object> returnedUser = getRes.jsonPath().getMap("");

        step("Verify returned user data", () -> {
            assertEquals(id, returnedUser.get("id"));
            assertEquals(email, returnedUser.get("email"));
            assertEquals(name, returnedUser.get("name"));
            assertEquals("male", returnedUser.get("gender"));
            assertEquals("active", returnedUser.get("status"));
        });

        step("End Test14", () -> log.info("User verified successfully.========== END Test14 =========="));
    }
}