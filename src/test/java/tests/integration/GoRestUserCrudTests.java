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

    private static final String TOKEN =
            System.getenv("GOREST_TOKEN") != null
                    ? System.getenv("GOREST_TOKEN")
                    : ConfigManager.get("gorest.token");

    static {
        if (TOKEN == null || TOKEN.isEmpty()) {
            throw new IllegalStateException(
                    "GOREST_TOKEN is not set! Set it in CI/CD secrets or in dev/qa properties."
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

        step("Prepare user payload", () -> {
            log.info("Creating user with email: {}", email);
            Allure.attachment("Request Payload", userPayload.toString());
        });

        // Create user
        Response createRes = step("Send POST request to create user", () -> api.createUser(userPayload));

        step("Log and attach create response", () -> {
            log.info("Create Status: {}", createRes.statusCode());
            Allure.attachment("Create Response", createRes.asString());
        });

        assertEquals(HttpStatus.SC_CREATED, createRes.statusCode(), "Expected 201 CREATED");
        Integer id = createRes.jsonPath().getInt("id");

        // GET user
        Response getRes = step("Send GET request to fetch user by ID", () -> api.getUser(id));

        step("Log and attach get response", () -> {
            log.info("Get Status: {}", getRes.statusCode());
            Allure.attachment("Get Response", getRes.asString());
        });

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