package clients;

import constant.ApiPaths;
import core.RequestSpecFactory;
import io.qameta.allure.Step;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

import java.util.Map;

import static io.restassured.RestAssured.given;

public class GoRestClient extends BaseClient {

    private final String token;

    public GoRestClient(String token) {
        super(RequestSpecFactory.gorest());
        this.token = token == null ? null : token.trim();
    }

    private io.restassured.specification.RequestSpecification base() {
        return given()
                .spec(requestSpec)
                .accept(ContentType.JSON)
                .contentType(ContentType.JSON)
                .header("User-Agent", "rest-assured-tests/1.0")
                .header("Authorization", "Bearer " + token);
    }

    @Step("Create User")
    public Response createUser(Map<String, Object> userData) {
        return base()
                .body(userData)
                .when()
                .post(ApiPaths.GOREST_USERS)
                .then()
                .extract()
                .response();
    }

    @Step("Get User")
    public Response getUser(Integer userId) {
        return base()
                .when()
                .get(ApiPaths.GOREST_USER_BY_ID, userId)
                .then()
                .extract()
                .response();
    }

    @Step("List Users (sanity check)")
    public Response listUsers() {
        return base()
                .when()
                .get(ApiPaths.GOREST_USERS)
                .then()
                .extract()
                .response();
    }
}