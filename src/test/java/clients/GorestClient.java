package clients;

import core.RequestSpecFactory;
import io.qameta.allure.Step;
import io.restassured.response.Response;

import java.util.Map;

import static io.restassured.RestAssured.given;

public class GorestClient extends BaseClient {

    private final String token;

    public GorestClient(String token) {
        super(RequestSpecFactory.gorest());
        this.token = token;
    }

    @Step("Create User")
    public Response createUser(Map<String, Object> userData) {

        Response res = given()
                .spec(requestSpec)
                .header("Authorization", "Bearer " + token)
                .body(userData)
                .when()
                .post("/users")
                .then()
                .extract()
                .response();

        return res;
    }

    @Step("Get User")
    public Response getUser(Integer userId) {

        Map<String, Object> requestInfo = Map.of("id", userId);

        Response res = given()
                .spec(requestSpec)
                .header("Authorization", "Bearer " + token)
                .when()
                .get("/users/{id}", userId)
                .then()
                .extract()
                .response();
        return res;
    }
}