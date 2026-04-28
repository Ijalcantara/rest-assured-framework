package tests.contactList;

import clients.ContactListClient;
import constant.ConstantClass;
import core.BaseApiTest;
import io.qameta.allure.*;
import io.restassured.response.Response;
import manager.TestDataManager;
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import utils.ApiAllureUtil;
import utils.AuthTokenUtil;
import utils.LoggerUtils;

import java.util.Map;
import java.util.UUID;

@Epic("ContactList API")
@Feature("ContactListEditContactTests")
@DisplayName("ContactListEditContactTests")
@Tag("checkForParallel")
public class ContactListEditContactTests extends BaseApiTest {

    private final ContactListClient api = new ContactListClient();
    private static final Logger log = LoggerUtils.getLogger(ContactListEditContactTests.class);

    @Test
    @Tag("auth")
    @DisplayName("TC01 - Edit success")
    public void edit1_contact_returns_200() {

        String runId = UUID.randomUUID().toString();

        Map<String, Object> loginPayload = TestDataManager.getDataAsMap(
                ConstantClass.CONTACT_LIST,
                ConstantClass.CONTACT_VALID_LOGIN
        );

        Response loginRes = api.login(loginPayload);
        String token = AuthTokenUtil.getToken(loginRes, "token");

        log.info("RUN_ID : {}", runId);
        log.info("TC     : TC01");

        LoggerUtils.logUserToken(log, "1st user", token);

        Map<String, Object> createPayload = TestDataManager.getDataAsMap(
                ConstantClass.CONTACT_LIST,
                ConstantClass.CONTACT_CREATE_CONTACT
        );

        LoggerUtils.logUserToken(log, "TEST 1", token);

        Response createRes = api.createContact(token, createPayload);
        LoggerUtils.logUserToken(log, "TEST 1.1", token);

        ApiAllureUtil.validateStatusCode(createRes, 201);

        String contactId = createRes.jsonPath().getString("_id");

        Map<String, Object> editPayload = TestDataManager.getDataAsMap(
                ConstantClass.CONTACT_LIST,
                ConstantClass.CONTACT_EDIT_CONTACT
        );

        Response editRes = api.editContact(token, contactId, editPayload);

        LoggerUtils.logUserToken(log, "1st user", token);

        ApiAllureUtil.validateStatusCode(editRes, 200);
        log.info(token, "1st user test");
    }

    @Test
    @Tag("auth")
    @DisplayName("TC02 - Edit success")
    public void edit2_contact_returns_200() {

        String runId = UUID.randomUUID().toString();

        Map<String, Object> loginPayload = TestDataManager.getDataAsMap(
                ConstantClass.CONTACT_LIST,
                ConstantClass.CONTACT_VALID_LOGIN2
        );

        Response loginRes = api.login(loginPayload);
        String token = AuthTokenUtil.getToken(loginRes, "token");

        log.info("RUN_ID : {}", runId);
        log.info("TC     : TC02");

        LoggerUtils.logUserToken(log, "2nd user", token);

        Map<String, Object> createPayload = TestDataManager.getDataAsMap(
                ConstantClass.CONTACT_LIST,
                ConstantClass.CONTACT_CREATE_CONTACT
        );

        LoggerUtils.logUserToken(log, "TEST 2", token);
        Response createRes = api.createContact(token, createPayload);
        LoggerUtils.logUserToken(log, "TEST 2.1", token);

        ApiAllureUtil.validateStatusCode(createRes, 201);

        String contactId = createRes.jsonPath().getString("_id");

        Map<String, Object> editPayload = TestDataManager.getDataAsMap(
                ConstantClass.CONTACT_LIST,
                ConstantClass.CONTACT_EDIT_CONTACT
        );

        Response editRes = api.editContact(token, contactId, editPayload);

        LoggerUtils.logUserToken(log, "2nd user", token);

        ApiAllureUtil.validateStatusCode(editRes, 200);
        log.info(token, "2nd user test");

    }

    @Test
    @Tag("auth")
    @DisplayName("TC03 - Edit success")
    public void edit3_contact_returns_200() {

        String runId = UUID.randomUUID().toString();

        Map<String, Object> loginPayload = TestDataManager.getDataAsMap(
                ConstantClass.CONTACT_LIST,
                ConstantClass.CONTACT_VALID_LOGIN3
        );

        Response loginRes = api.login(loginPayload);
        String token = AuthTokenUtil.getToken(loginRes, "token");

        log.info("RUN_ID : {}", runId);
        log.info("TC     : TC03");

        LoggerUtils.logUserToken(log, "3rd user", token);

        Map<String, Object> createPayload = TestDataManager.getDataAsMap(
                ConstantClass.CONTACT_LIST,
                ConstantClass.CONTACT_CREATE_CONTACT
        );

        LoggerUtils.logUserToken(log, "TEST 3", token);
        Response createRes = api.createContact(token, createPayload);
        LoggerUtils.logUserToken(log, "TEST 3.1", token);

        ApiAllureUtil.validateStatusCode(createRes, 201);

        String contactId = createRes.jsonPath().getString("_id");

        Map<String, Object> editPayload = TestDataManager.getDataAsMap(
                ConstantClass.CONTACT_LIST,
                ConstantClass.CONTACT_EDIT_CONTACT
        );

        Response editRes = api.editContact(token, contactId, editPayload);

        LoggerUtils.logUserToken(log, "3rd user", token);

        ApiAllureUtil.validateStatusCode(editRes, 200);
        log.info(token, "3rd user test");
    }

    @Test
    @Tag("auth")
    @DisplayName("TC04 - Edit success")
    public void edit4_contact_returns_200() {

        String runId = UUID.randomUUID().toString();

        Map<String, Object> loginPayload = TestDataManager.getDataAsMap(
                ConstantClass.CONTACT_LIST,
                ConstantClass.CONTACT_VALID_LOGIN4
        );

        Response loginRes = api.login(loginPayload);
        String token = AuthTokenUtil.getToken(loginRes, "token");

        log.info("RUN_ID : {}", runId);
        log.info("TC     : TC04");

        LoggerUtils.logUserToken(log, "4th user", token);

        Map<String, Object> createPayload = TestDataManager.getDataAsMap(
                ConstantClass.CONTACT_LIST,
                ConstantClass.CONTACT_CREATE_CONTACT
        );

        LoggerUtils.logUserToken(log, "TEST 4", token);
        Response createRes = api.createContact(token, createPayload);
        LoggerUtils.logUserToken(log, "TEST 4.1", token);

        ApiAllureUtil.validateStatusCode(createRes, 201);

        String contactId = createRes.jsonPath().getString("_id");

        Map<String, Object> editPayload = TestDataManager.getDataAsMap(
                ConstantClass.CONTACT_LIST,
                ConstantClass.CONTACT_EDIT_CONTACT
        );

        Response editRes = api.editContact(token, contactId, editPayload);

        LoggerUtils.logUserToken(log, "4th user", token);

        ApiAllureUtil.validateStatusCode(editRes, 200);
        log.info(token, "4th user test");

    }

    @Test
    @Tag("auth")
    @DisplayName("TC05 - Edit success")
    public void edit5_contact_returns_200() {

        String runId = UUID.randomUUID().toString();

        Map<String, Object> loginPayload = TestDataManager.getDataAsMap(
                ConstantClass.CONTACT_LIST,
                ConstantClass.CONTACT_VALID_LOGIN5
        );

        Response loginRes = api.login(loginPayload);
        String token = AuthTokenUtil.getToken(loginRes, "token");

        log.info("RUN_ID : {}", runId);
        log.info("TC     : TC05");

        LoggerUtils.logUserToken(log, "5th user", token);

        Map<String, Object> createPayload = TestDataManager.getDataAsMap(
                ConstantClass.CONTACT_LIST,
                ConstantClass.CONTACT_CREATE_CONTACT
        );

        LoggerUtils.logUserToken(log, "TEST 5", token);
        Response createRes = api.createContact(token, createPayload);
        LoggerUtils.logUserToken(log, "TEST 5.1", token);

        ApiAllureUtil.validateStatusCode(createRes, 201);

        String contactId = createRes.jsonPath().getString("_id");

        Map<String, Object> editPayload = TestDataManager.getDataAsMap(
                ConstantClass.CONTACT_LIST,
                ConstantClass.CONTACT_EDIT_CONTACT
        );

        Response editRes = api.editContact(token, contactId, editPayload);

        LoggerUtils.logUserToken(log, "5th user", token);

        ApiAllureUtil.validateStatusCode(editRes, 200);


        log.info(token, "5th user test");
    }
}