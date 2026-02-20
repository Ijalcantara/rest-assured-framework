package clients;

import core.RequestSpecFactory;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

public class DummyJsonClient extends BaseClient {

    public DummyJsonClient() {
        super(RequestSpecFactory.dummyJson());
    }

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