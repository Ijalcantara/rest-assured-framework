package clients;

import core.RequestSpecFactory;
import io.restassured.response.Response;

import java.util.Map;

import static io.restassured.RestAssured.given;

public class GorestClient extends BaseClient {

    private final String token;

    public GorestClient(String token) {
        super(RequestSpecFactory.gorest());
        this.token = token;
    }

    public Response createUser(Map<String, Object> userData) {
        return given()
                .spec(getRequestSpec())
                .header("Authorization", "Bearer " + token)
                .body(userData)
                .when()
                .post("/users")
                .then()
                .extract()
                .response();
    }

    public Response getUser(Integer userId) {
        return given()
                .spec(getRequestSpec())  // ✅ use getter
                .header("Authorization", "Bearer " + token)
                .when()
                .get("/users/{id}", userId)
                .then()
                .extract()
                .response();
    }
}