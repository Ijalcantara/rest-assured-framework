package clients;

import constant.EndpointConstant;
import core.RequestSpecFactory;
import io.qameta.allure.Step;
import io.restassured.response.Response;

import java.util.Map;

import static io.restassured.RestAssured.given;

public class DummyJsonClient extends BaseClient {

    public DummyJsonClient() {
        super(RequestSpecFactory.dummyJson());
    }

    /**
     * Login API
     */
    @Step("Login with body: {body}")
    public Response login(Object body) {
        return given()
                .spec(requestSpec)
                .body(body != null ? body : "{}")
                .when()
                .post(EndpointConstant.LOGIN)
                .then()
                .extract()
                .response();
    }

    /**
     * Get /user/me with dynamic token
     */
    @Step("Get /user/me with token")
    public Response userMe(String token) {
        return given()
                .spec(requestSpec)
                .header("Authorization", "Bearer " + token)
                .when()
                .get(EndpointConstant.USER_ME)
                .then()
                .extract()
                .response();
    }

    /**
     * Search users with dynamic token
     */
    @Step("Search users with query: {query}")
    public Response searchUsers(String token, String query) {
        Map<String, String> queryParams = Map.of("q", query);

        return given()
                .spec(requestSpec)
                .header("Authorization", "Bearer " + token)
                .queryParams(queryParams)
                .when()
                .get("/users/search")
                .then()
                .extract()
                .response();
    }
}