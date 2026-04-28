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
import org.junit.jupiter.api.TestInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.ApiAllureUtil;
import utils.AuthTokenUtil;

import java.util.Map;

@Epic("ContactList API")
@Feature("ContactListCreateContactTests")
@DisplayName("ContactListCreateContactTests")
@Tag("contact")
public class ContactListCreateContactTests extends BaseApiTest {

    private final ContactListClient api = new ContactListClient();
    private static final Logger log = LoggerFactory.getLogger(BaseApiTest.class);

    @Story(ConstantClass.STORY_REGISTRATION_SUCCESS)
    @Test
    @Tag("auth")
    @DisplayName("TC01 - Contact creation success")
    void create_contact_returns_201() {

        Map<String, Object> loginRequestPayload = TestDataManager.getDataAsMap(
                ConstantClass.CONTACT_LIST,
                ConstantClass.CONTACT_VALID_LOGIN
        );

        Response loginRes = api.login(loginRequestPayload);

        String token = AuthTokenUtil.getToken(loginRes, "token"); // static token

        Map<String, Object> requestPayload = TestDataManager.getDataAsMap(
                ConstantClass.CONTACT_LIST,
                ConstantClass.CONTACT_CREATE_CONTACT
        );
        Response res = api.createContact(token, requestPayload);

        log.info("This is the token passed: " + token);

        ApiAllureUtil.logScenario("User create a new contact");
        ApiAllureUtil.logRequestPayload(requestPayload);
        log.info("This is the response: " + res.asString());
        ApiAllureUtil.validateStatusCode(res, 201);

        ApiAllureUtil.attachApiCall(requestPayload, res);
    }
}