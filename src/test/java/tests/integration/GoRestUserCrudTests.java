package tests.integration;

import clients.GorestClient;
import config.ConfigManager;
import constant.ConstantClass;
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
    void create_then_get_user_should_match() throws Exception {

        String unique = String.valueOf(System.currentTimeMillis());
        String email = "user" + unique + "@example.com";
        String name = "Automation User " + unique;

        Map<String, Object> userPayload = new HashMap<>();
        userPayload.put(ConstantClass.FIELD_NAME, name);
        userPayload.put(ConstantClass.GOREST_FIELD_EMAIL, email);
        userPayload.put(ConstantClass.FIELD_GENDER, "male");
        userPayload.put(ConstantClass.FIELD_STATUS, "active");

        // ===== CREATE USER STEP =====
        Allure.step("Create User", () -> {

            Response createRes = api.createUser(userPayload);

            ReusableMethod.attachApiCallUnified(userPayload, createRes, null);

            ReusableMethod.validateRequestSection(userPayload,
                    ConstantClass.FIELD_NAME,
                    ConstantClass.GOREST_FIELD_EMAIL,
                    ConstantClass.FIELD_GENDER,
                    ConstantClass.FIELD_STATUS);
            ReusableMethod.validateStatusSection(createRes, HttpStatus.SC_CREATED);
            ReusableMethod.validateResponseSection(createRes,
                    ConstantClass.GOREST_FIELD_ID,
                    ConstantClass.FIELD_NAME,
                    ConstantClass.GOREST_FIELD_EMAIL,
                    ConstantClass.FIELD_GENDER,
                    ConstantClass.FIELD_STATUS);

            // Store created ID for GET
            userPayload.put(ConstantClass.GOREST_FIELD_ID,
                    createRes.jsonPath().getInt(ConstantClass.GOREST_FIELD_ID));
        });

        Integer id = (Integer) userPayload.get(ConstantClass.GOREST_FIELD_ID);

        // ===== GET USER STEP =====
        Allure.step("Get User", () -> {

            Map<String, Object> getRequestInfo = Map.of(ConstantClass.GOREST_FIELD_USER_ID, id);
            Response getRes = api.getUser(id);

            ReusableMethod.attachApiCallUnified(getRequestInfo, getRes, null);

            ReusableMethod.validateRequestSection(getRequestInfo, ConstantClass.GOREST_FIELD_USER_ID);
            ReusableMethod.validateStatusSection(getRes, HttpStatus.SC_OK);
            ReusableMethod.validateResponseSection(getRes,
                    ConstantClass.GOREST_FIELD_ID,
                    ConstantClass.FIELD_NAME,
                    ConstantClass.GOREST_FIELD_EMAIL,
                    ConstantClass.FIELD_GENDER,
                    ConstantClass.FIELD_STATUS);

            Map<String, Object> expectedFields = Map.of(
                    ConstantClass.GOREST_FIELD_ID, id,
                    ConstantClass.FIELD_NAME, name,
                    ConstantClass.GOREST_FIELD_EMAIL, email,
                    ConstantClass.FIELD_GENDER, "male",
                    ConstantClass.FIELD_STATUS, "active"
            );
            ReusableMethod.validateResponseFields(getRes, expectedFields);
        });
    }
}