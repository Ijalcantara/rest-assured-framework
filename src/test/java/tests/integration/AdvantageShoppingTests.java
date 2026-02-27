package tests.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import constant.ConstantClass;
import core.BaseApiTest;
import core.RequestSpecFactory;
import core.TestDataManager;
import io.qameta.allure.*;
import io.restassured.builder.ResponseBuilder;
import io.restassured.response.Response;
import org.junit.jupiter.api.*;
import utils.reusablemethod.ReusableMethod;

import java.util.HashMap;
import java.util.Map;

@Epic("Advantage Shopping API")
@Feature("User Registration")
@DisplayName("AdvantageShoppingTests")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class AdvantageShoppingTests extends BaseApiTest {

    @Test
    @Tag("#TC01")
    @DisplayName("TC01 - Wrong API Version should return 404")
    void wrong_login_version_should_return_404() {

        Map<String, Object> body = TestDataManager.getDataAsMap(
                ConstantClass.ADVANTAGE_SHOPPING, "registerUser"
        );

        Response res = io.restassured.RestAssured.given()
                .spec(RequestSpecFactory.advantage())
                .body(body)
                .when()
                .post("/register");

        ReusableMethod.validateApiScenario(
                "Attempt to register a user with a wrong API version.",
                body,
                res,
                404
        );

        ReusableMethod.attachApiCall(body, res);
    }

    @Test
    @Tag("#TC02")
    @DisplayName("TC02 - Register new user should return success (mock)")
    void register_new_user_should_return_success_mock() throws Exception {

        Map<String, Object> userPayload = TestDataManager.getDataAsMap(
                ConstantClass.ADVANTAGE_SHOPPING, "registerUser"
        );

        long timestamp = System.currentTimeMillis();
        userPayload.put(ConstantClass.FIELD_EMAIL, "automation" + timestamp + "@example.com");
        userPayload.put(ConstantClass.FIELD_LOGIN_NAME, "auto" + timestamp);

        Map<String, Object> responseBody = Map.of(
                ConstantClass.FIELD_SUCCESS, true,
                ConstantClass.FIELD_USER_ID, 12345,
                ConstantClass.FIELD_REASON, "User created successfully"
        );

        String responseJson = new ObjectMapper().writeValueAsString(responseBody);

        Response mockRes = new ResponseBuilder()
                .setStatusCode(200)
                .setBody(responseJson)
                .build();

        ReusableMethod.validateApiScenario(
                "Simulate registering a new user with valid details.",
                userPayload,
                mockRes,
                200,
                ConstantClass.FIELD_SUCCESS, ConstantClass.FIELD_USER_ID
        );

        ReusableMethod.attachApiCall(userPayload, mockRes);
    }
}

//package tests.integration;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import constant.ConstantClass;
//import core.BaseApiTest;
//import core.RequestSpecFactory;
//import core.TestDataManager;
//import io.qameta.allure.*;
//import io.restassured.builder.ResponseBuilder;
//import io.restassured.response.Response;
//import org.junit.jupiter.api.*;
//import utils.reusablemethod.ReusableMethod;
//
//import java.util.HashMap;
//import java.util.Map;
//
//@Epic("Advantage Shopping API")
//@Feature("User Registration")
//@DisplayName("AdvantageShoppingTests")
//@TestInstance(TestInstance.Lifecycle.PER_CLASS)
//public class AdvantageShoppingTests extends BaseApiTest {
//
//    @Test
//    @Tag("#TC01")
//    @DisplayName("TC01 - Wrong API Version should return 404")
//    @Story("Wrong API Version should return 404")
//    @Description("Test verifies that using a wrong API version for /register endpoint returns 404")
//    void wrong_login_version_should_return_404() {
//
//        Map<String, Object> body = TestDataManager.getDataAsMap(
//                ConstantClass.ADVANTAGE_SHOPPING,
//                "registerUser"
//        );
//
//        Response res = io.restassured.RestAssured.given()
//                .spec(RequestSpecFactory.advantage())
//                .body(body)
//                .when()
//                .post("/register");
//
//        ReusableMethod.attachApiCall(body, res);
//        ReusableMethod.attachBusinessSummary(
//                "Attempt to register a user with a wrong API version."
//        );
//    }
//
//    @Test
//    @Tag("#TC02")
//    @DisplayName("TC02 - Register new user should return success (mock)")
//    @Story("Register new user should return success (mock)")
//    @Description("Test simulates registering a new user and validates the mocked success response")
//    void register_new_user_should_return_success_mock() throws Exception {
//
//        Map<String, Object> userPayload = TestDataManager.getDataAsMap(
//                ConstantClass.ADVANTAGE_SHOPPING,
//                "registerUser"
//        );
//
//        long timestamp = System.currentTimeMillis();
//        userPayload.put(ConstantClass.FIELD_EMAIL, "automation" + timestamp + "@example.com");
//        userPayload.put(ConstantClass.FIELD_LOGIN_NAME, "auto" + timestamp);
//
//        // ===== MOCK Response Body =====
//        Map<String, Object> responseBody = Map.of(
//                ConstantClass.FIELD_SUCCESS, true,
//                ConstantClass.FIELD_USER_ID, 12345,
//                ConstantClass.FIELD_REASON, "User created successfully"
//        );
//
//        String responseJson = new ObjectMapper().writeValueAsString(responseBody);
//
//        // ===== MOCK Response =====
//        Response mockRes = new ResponseBuilder()
//                .setStatusCode(200)
//                .setBody(responseJson)
//                .build();
//
//        int statusCode = mockRes.getStatusCode();
//        ReusableMethod.attachApiWithMockResponse(userPayload, statusCode, mockRes);
//        ReusableMethod.attachBusinessSummary(
//                "Simulate registering a new user with valid details."
//        );
//    }
//}