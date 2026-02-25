package tests.integration;

import clients.GorestClient;
import config.ConfigManager;
import core.BaseApiTest;
import io.qameta.allure.*;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.reusablemethod.ReusableMethod;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Epic("GoRest API")
@Feature("User CRUD Operations")
@DisplayName("GoRestUserCrudTests")
public class GoRestUserCrudTests extends BaseApiTest {

    private static final Logger log = LoggerFactory.getLogger(GoRestUserCrudTests.class);

    private static final String TOKEN =
            System.getenv("GOREST_TOKEN") != null
                    ? System.getenv("GOREST_TOKEN")
                    : ConfigManager.get("gorest.token");

    private final GorestClient api = new GorestClient(TOKEN);

    @Test
    @Tag("#TC01")
    @DisplayName("TC01 - Create a new user and verify via GET")
    @Story("Create and GET user")
    @Description("Test verifies that creating a new GoRest user and fetching it by ID returns correct data")
    void create_then_get_user_should_match() {

        String unique = String.valueOf(System.currentTimeMillis());
        String email = "user" + unique + "@example.com";
        String name = "Automation User " + unique;

        Map<String, Object> userPayload = new HashMap<>();
        userPayload.put("name", name);
        userPayload.put("email", email);
        userPayload.put("gender", "male");
        userPayload.put("status", "active");

        // 🔹 CREATE USER
        Response createRes = api.createUser(userPayload);
        Allure.step("API Request / Response (Create)",
                () -> ReusableMethod.attachApiCall(userPayload, createRes));
        assertEquals(HttpStatus.SC_CREATED, createRes.statusCode(), "Expected 201 CREATED");

        Integer id = createRes.jsonPath().getInt("id");

        // 🔹 GET USER
        Map<String, Object> getRequestInfo = Map.of("userId", id);
        Response getRes = api.getUser(id);
        Allure.step("API Request / Response (Get)",
                () -> ReusableMethod.attachApiCall(getRequestInfo, getRes));
        assertEquals(HttpStatus.SC_OK, getRes.statusCode(), "Expected 200 OK");

        Map<String, Object> returnedUser = getRes.jsonPath().getMap("");

        assertEquals(id, returnedUser.get("id"));
        assertEquals(email, returnedUser.get("email"));
        assertEquals(name, returnedUser.get("name"));
        assertEquals("male", returnedUser.get("gender"));
        assertEquals("active", returnedUser.get("status"));
    }
}