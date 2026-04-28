package clients;

import constant.EndpointConstant;
import core.RequestSpecFactory;
import io.qameta.allure.Step;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

public class ContactListClient extends BaseClient {

    public ContactListClient() {
        super(RequestSpecFactory.contactList());
    }

    /**
     * Create User
     */
    @Step("Create new user")
    public Response registerUser(Object body) {
        return given()
                .spec(requestSpec)
                .body(body != null ? body : "{}")
                .when()
                .post(EndpointConstant.CONTACT_LIST_CREATE_USER)
                .then()
                .extract()
                .response();
    }

    /**
     * Login User
     */
    @Step("Login user")
    public Response login(Object body) {
        return given()
                .spec(requestSpec)
                .body(body != null ? body : "{}")
                .when()
                .post(EndpointConstant.CONTACT_LIST_LOGIN)
                .then()
                .extract()
                .response();
    }

    /**
     * Create Contact
     */
    @Step("Create contact")
    public Response createContact(String token, Object body) {
        return given()
                .spec(requestSpec)
                .header("Authorization", "Bearer " + token)
                .body(body != null ? body : "{}")
                .when()
                .post(EndpointConstant.CONTACT_LIST_CONTACTS)
                .then()
                .extract()
                .response();
    }

    /**
     * DELETE /contacts/{id}
     * Delete contact
     */
    @Step("Delete contact with id: {contactId}")
    public Response deleteContact(String token, String contactId) {
        return given()
                .spec(requestSpec)
                .header("Authorization", "Bearer " + token)
                .when()
                .delete(EndpointConstant.CONTACT_LIST_DELETE_CONTACT + contactId)
                .then()
                .extract()
                .response();
    }

    @Step("Get all contacts")
    public Response getContacts(String token) {
        return given()
                .spec(requestSpec)
                .header("Authorization", "Bearer " + token)
                .when()
                .get(EndpointConstant.CONTACT_LIST_CONTACTS)
                .then()
                .extract()
                .response();
    }

    @Step("Edit contact")
    public Response editContact(String token, String contactId, Object body) {
        return given()
                .spec(requestSpec)
                .header("Authorization", "Bearer " + token)
                .body(body != null ? body : "{}")
                .when()
                .patch(EndpointConstant.CONTACT_LIST_CONTACTS + "/" + contactId)
                .then()
                .extract()
                .response();
    }
}

