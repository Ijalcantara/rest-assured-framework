package tests.contactList;

import clients.ContactListClient;
import constant.ConstantClass;
import core.BaseApiTest;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import io.restassured.response.Response;
import manager.TestDataManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.ApiAllureUtil;
import utils.ApiTestUtils;

import java.util.Map;

@Epic("ContactList API")
@Feature("ContactListRegisterUserTests")
@DisplayName("ContactListRegisterUserTests")
@Tag("forToken")
@SuppressWarnings("java:S106")
public class ContactListRegisterUserTests extends BaseApiTest {

    private final ContactListClient api = new ContactListClient();
    private static final Logger log = LoggerFactory.getLogger(ContactListRegisterUserTests.class);

    @Story(ConstantClass.STORY_REGISTRATION_SUCCESS)
    @Test
    @Tag("auth")
    @DisplayName("TC01 - Registration success")
    public void register_user_returns_201() {
        Map<String, Object> requestPayload = TestDataManager.getDataAsMap(
                ConstantClass.CONTACT_LIST,
                ConstantClass.CONTACT_REGISTER_USER
        );

        log.info("Original payload: {}", requestPayload);

        // Make email unique for this test run
        requestPayload = ApiTestUtils.makeUniqueEmail(requestPayload, "email");
        log.info("Payload after making email unique: {}", requestPayload);

        log.info("Sending registration request...");
        Response res = api.registerUser(requestPayload);
        log.info("Response received: {}", res.asPrettyString());

        ApiAllureUtil.logScenario("User registers with valid credentials.");
        ApiAllureUtil.logRequestPayload(requestPayload);

        ApiAllureUtil.validateStatusCode(res, 201);
        ApiAllureUtil.attachApiCall(requestPayload, res);
    }

    // --- Repeat for other test cases with logging ---
    @Story(ConstantClass.STORY_REGISTRATION_SUCCESS)
    @Test
    @Tag("auth")
    @DisplayName("TC02 - Registration success")
    public void register2_user_returns_201() {
        Map<String, Object> requestPayload = TestDataManager.getDataAsMap(
                ConstantClass.CONTACT_LIST,
                ConstantClass.CONTACT_REGISTER_USER2
        );

        log.info("Original payload: {}", requestPayload);
        requestPayload = ApiTestUtils.makeUniqueEmail(requestPayload, "email");
        log.info("Payload after making email unique: {}", requestPayload);

        log.info("Sending registration request...");
        Response res = api.registerUser(requestPayload);
        log.info("Response received: {}", res.asPrettyString());

        ApiAllureUtil.logScenario("User registers with valid credentials.");
        ApiAllureUtil.logRequestPayload(requestPayload);

        ApiAllureUtil.validateStatusCode(res, 201);
        ApiAllureUtil.attachApiCall(requestPayload, res);
    }

    @Story(ConstantClass.STORY_REGISTRATION_SUCCESS)
    @Test
    @Tag("auth")
    @DisplayName("TC03 - Registration success")
    public void register3_user_returns_201() {
        Map<String, Object> requestPayload = TestDataManager.getDataAsMap(
                ConstantClass.CONTACT_LIST,
                ConstantClass.CONTACT_REGISTER_USER3
        );

        log.info("Original payload: {}", requestPayload);
        requestPayload = ApiTestUtils.makeUniqueEmail(requestPayload, "email");
        log.info("Payload after making email unique: {}", requestPayload);

        log.info("Sending registration request...");
        Response res = api.registerUser(requestPayload);
        log.info("Response received: {}", res.asPrettyString());

        ApiAllureUtil.logScenario("User registers with valid credentials.");
        ApiAllureUtil.logRequestPayload(requestPayload);

        ApiAllureUtil.validateStatusCode(res, 201);
        ApiAllureUtil.attachApiCall(requestPayload, res);
    }

    @Story(ConstantClass.STORY_REGISTRATION_SUCCESS)
    @Test
    @Tag("auth")
    @DisplayName("TC04 - Registration success")
    public void register4_user_returns_201() {
        Map<String, Object> requestPayload = TestDataManager.getDataAsMap(
                ConstantClass.CONTACT_LIST,
                ConstantClass.CONTACT_REGISTER_USER4
        );

        log.info("Original payload: {}", requestPayload);
        requestPayload = ApiTestUtils.makeUniqueEmail(requestPayload, "email");
        log.info("Payload after making email unique: {}", requestPayload);

        log.info("Sending registration request...");
        Response res = api.registerUser(requestPayload);
        log.info("Response received: {}", res.asPrettyString());

        ApiAllureUtil.logScenario("User registers with valid credentials.");
        ApiAllureUtil.logRequestPayload(requestPayload);

        ApiAllureUtil.validateStatusCode(res, 201);
        ApiAllureUtil.attachApiCall(requestPayload, res);
    }

    @Story(ConstantClass.STORY_REGISTRATION_SUCCESS)
    @Test
    @Tag("auth")
    @DisplayName("TC05 - Registration success")
    public void register5_user_returns_201() {
        Map<String, Object> requestPayload = TestDataManager.getDataAsMap(
                ConstantClass.CONTACT_LIST,
                ConstantClass.CONTACT_REGISTER_USER5
        );

        log.info("Original payload: {}", requestPayload);
        requestPayload = ApiTestUtils.makeUniqueEmail(requestPayload, "email");
        log.info("Payload after making email unique: {}", requestPayload);

        log.info("Sending registration request...");
        Response res = api.registerUser(requestPayload);
        log.info("Response received: {}", res.asPrettyString());

        ApiAllureUtil.logScenario("User registers with valid credentials.");
        ApiAllureUtil.logRequestPayload(requestPayload);

        ApiAllureUtil.validateStatusCode(res, 201);
        ApiAllureUtil.attachApiCall(requestPayload, res);
    }
}