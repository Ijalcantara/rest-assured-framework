package clients;

import core.RequestSpecFactory;
import io.qameta.allure.Step;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

public class DummyJsonClient extends BaseClient {

    public DummyJsonClient() {
        super(RequestSpecFactory.dummyJson());
    }

    @Step("Login with body {body}")
    public Response login(Object body) {
        return given()
                .spec(getRequestSpec())
                .body(body)
                .when()
                .post("/auth/login")
                .then()
                .extract()
                .response();
    }

    @Step("Get /user/me with access token {accessToken}")
    public Response userMe(String accessToken) {
        return given()
                .spec(getRequestSpec())
                .header("Authorization", "Bearer " + accessToken)
                .header("Accept-Encoding", "identity")
                .when()
                .get("/user/me")
                .then()
                .extract()
                .response();
    }

    @Step("Search users with query {q}")
    public Response searchUsers(String q) {
        return given()
                .spec(getRequestSpec())
                .queryParam("q", q)
                .when()
                .get("/users/search")
                .then()
                .extract()
                .response();
    }
}