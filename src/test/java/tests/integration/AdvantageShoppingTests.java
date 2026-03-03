package tests.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import constant.ConstantClass;
import core.BaseApiTest;
import core.RequestSpecFactory;
import manager.TestDataManager;
import io.qameta.allure.*;
import io.restassured.builder.ResponseBuilder;
import io.restassured.response.Response;
import org.junit.jupiter.api.*;
import utils.ApiAllureUtil;

import java.util.Map;

@Epic("Advantage Shopping API")
@Feature("User Registration")
@DisplayName("AdvantageShoppingTests")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class AdvantageShoppingTests extends BaseApiTest {

    // =====================================================
    // TC01 - Wrong API Version
    // =====================================================
    @Test
    @Tag("#TC01")
    @DisplayName("TC01 - Wrong API Version should return 404")
    void wrong_login_version_should_return_404() {

        Map<String, Object> body = TestDataManager.getDataAsMap(
                ConstantClass.ADVANTAGE_SHOPPING,
                "registerUser"
        );

        Response res = io.restassured.RestAssured.given()
                .spec(RequestSpecFactory.advantage())
                .body(body)
                .when()
                .post("/register");

        ApiAllureUtil.logScenario(
                "Attempt to register a user with a wrong API version."
        );

        ApiAllureUtil.validateStatusCode(res, 404);

        // No required fields for negative scenario
        ApiAllureUtil.validateResponseBody(res);

        ApiAllureUtil.attachApiCall(body, res);
    }

    // =====================================================
    // TC02 - Mock Registration Success
    // =====================================================
    @Test
    @Tag("#TC02")
    @DisplayName("TC02 - Register new user should return success (mock)")
    void register_new_user_should_return_success_mock() throws Exception {

        Map<String, Object> userPayload = TestDataManager.getDataAsMap(
                ConstantClass.ADVANTAGE_SHOPPING,
                "registerUser"
        );

        long timestamp = System.currentTimeMillis();
        userPayload.put(
                ConstantClass.FIELD_EMAIL,
                "automation" + timestamp + "@example.com"
        );
        userPayload.put(
                ConstantClass.FIELD_LOGIN_NAME,
                "auto" + timestamp
        );

        Map<String, Object> responseBody = Map.of(
                ConstantClass.FIELD_SUCCESS, true,
                ConstantClass.FIELD_USER_ID, 12345,
                ConstantClass.FIELD_REASON, "User created successfully"
        );

        String responseJson =
                new ObjectMapper().writeValueAsString(responseBody);

        Response mockRes = new ResponseBuilder()
                .setStatusCode(200)
                .setBody(responseJson)
                .build();

        ApiAllureUtil.logScenario(
                "Simulate registering a new user with valid details."
        );

        ApiAllureUtil.validateStatusCode(mockRes, 200);

        ApiAllureUtil.validateResponseBody(
                mockRes,
                ConstantClass.FIELD_SUCCESS,
                ConstantClass.FIELD_USER_ID
        );

        ApiAllureUtil.attachApiCall(userPayload, mockRes);
    }
}