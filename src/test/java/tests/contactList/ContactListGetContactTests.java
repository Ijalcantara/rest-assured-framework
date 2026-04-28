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

//@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Epic("ContactList API")
@Feature("ContactListLoginTests")
@DisplayName("ContactListLoginTests")
@Tag("toTest")
public class ContactListGetContactTests extends BaseApiTest {

    private final ContactListClient api = new ContactListClient();
    private static final Logger log = LoggerFactory.getLogger(BaseApiTest.class);

    @Story(ConstantClass.STORY_REGISTRATION_SUCCESS)
    @Test
    @DisplayName("TC01 - Login success")
    public void login_user_returns_200() {

        // ================= LOGIN =================
        Map<String, Object> loginRequestPayload = TestDataManager.getDataAsMap(
                ConstantClass.CONTACT_LIST,
                ConstantClass.CONTACT_VALID_LOGIN
        );

        Response loginRes = api.login(loginRequestPayload);

        log.info("LOGIN STATUS: " + loginRes.statusCode());
        log.info("LOGIN BODY: " + loginRes.asString());

        // 🔥 HARD GUARD (CRITICAL FIX)
        if (loginRes.statusCode() != 200) {
            throw new RuntimeException(
                    "❌ Login failed. Check CONTACT_VALID_LOGIN. Response: "
                            + loginRes.asString()
            );
        }

        String token = AuthTokenUtil.getToken(loginRes, "token");

        // ================= GET CONTACTS =================
        Response res = api.getContacts(token);

        log.info("GET CONTACTS STATUS: " + res.statusCode());
        log.info("GET CONTACTS BODY: " + res.asString());

        ApiAllureUtil.validateStatusCode(res, 200);

        ApiAllureUtil.logScenario("Get the List of Contacts for the logged in user.");

        log.info("Response:\n" + res.prettyPrint());

        // ❌ REMOVE THIS (WRONG ASSERTION)
        // ApiAllureUtil.validateResponseBody(res, "token", "user.email");
    }

}