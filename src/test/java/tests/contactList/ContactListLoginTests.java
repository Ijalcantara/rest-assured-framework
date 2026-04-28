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
import utils.ApiAllureUtil;
import utils.AuthTokenUtil;

import java.util.Map;

//@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Epic("ContactList API")
@Feature("ContactListLoginTests")
@DisplayName("ContactListLoginTests")
@Tag("contact")
public class ContactListLoginTests extends BaseApiTest {

    private final ContactListClient api = new ContactListClient();

    @Story(ConstantClass.STORY_REGISTRATION_SUCCESS)
    @Test
    @Tag("auth")
    @DisplayName("TC01 - Login success")
    public void login_user_returns_200() {

        Map<String, Object> requestPayload = TestDataManager.getDataAsMap(
                ConstantClass.CONTACT_LIST,
                ConstantClass.CONTACT_VALID_LOGIN
        );

        Response res = api.login(requestPayload);

        ApiAllureUtil.logScenario("User log in with valid credentials.");
        ApiAllureUtil.logRequestPayload(requestPayload);

        ApiAllureUtil.validateStatusCode(res, 200);

        ApiAllureUtil.validateResponseBody(res, "token", "user.email");
        AuthTokenUtil.getToken(res, "token"); // static token

        ApiAllureUtil.attachApiCall(requestPayload, res);
    }

    @Story(ConstantClass.STORY_REGISTRATION_SUCCESS)
    @Test
    @Tag("auth")
    @DisplayName("TC02 - Login success")
    public void login2_user_returns_200() {

        Map<String, Object> requestPayload = TestDataManager.getDataAsMap(
                ConstantClass.CONTACT_LIST,
                ConstantClass.CONTACT_VALID_LOGIN
        );

        Response res = api.login(requestPayload);

        ApiAllureUtil.logScenario("User log in with valid credentials.");
        ApiAllureUtil.logRequestPayload(requestPayload);

        ApiAllureUtil.validateStatusCode(res, 200);

        ApiAllureUtil.validateResponseBody(res, "token", "user.email");
        AuthTokenUtil.getToken(res, "token");

        ApiAllureUtil.attachApiCall(requestPayload, res);
    }


}