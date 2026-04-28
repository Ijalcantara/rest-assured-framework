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
@Feature("ContactListDeleteContactTests")
@DisplayName("ContactListDeleteContactTests")
@Tag("contact")
public class ContactListDeleteUserTests extends BaseApiTest {

    private final ContactListClient api = new ContactListClient();
    private static final Logger log = LoggerFactory.getLogger(BaseApiTest.class);

    @Story(ConstantClass.STORY_REGISTRATION_SUCCESS)
    @Test
    @DisplayName("TC01 - Deletion success")
    public void delete_contact_returns_200() {

        // ================= LOGIN =================
        Map<String, Object> loginRequestPayload = TestDataManager.getDataAsMap(
                ConstantClass.CONTACT_LIST,
                ConstantClass.CONTACT_VALID_LOGIN
        );

        Response loginRes = api.login(loginRequestPayload);

        log.info("LOGIN STATUS: " + loginRes.statusCode());
        log.info("LOGIN BODY: " + loginRes.asString());

        // 🔥 STOP EARLY IF INVALID
        if (loginRes.statusCode() != 200) {
            throw new RuntimeException(
                    "❌ Login failed. CONTACT_VALID_LOGIN is invalid for this run. Response: "
                            + loginRes.asString()
            );
        }

//        String token = AuthTokenUtil.getToken(loginRes, "token");
        String token = loginRes.jsonPath().getString("token");

        // ================= CREATE CONTACT =================
        Map<String, Object> createPayload = TestDataManager.getDataAsMap(
                ConstantClass.CONTACT_LIST,
                ConstantClass.CONTACT_CREATE_CONTACT
        );

        Response createRes = api.createContact(token, createPayload);

        ApiAllureUtil.validateStatusCode(createRes, 201);

        String contactId = createRes.jsonPath().getString("_id");

        // ================= DELETE CONTACT =================
        Response deleteRes = api.deleteContact(token, contactId);

        log.info("DELETE STATUS: " + deleteRes.statusCode());
        log.info("DELETE BODY: " + deleteRes.asString());

        ApiAllureUtil.validateStatusCode(deleteRes, 200);

        log.info("DELETE SUCCESS: " + deleteRes.asPrettyString());
    }
}