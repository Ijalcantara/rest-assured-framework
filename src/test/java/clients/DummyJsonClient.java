package clients;

import core.RequestSpecFactory;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import utils.reusablemethod.ReusableMethod;

import java.util.Map;

public class DummyJsonClient extends BaseClient {

    public DummyJsonClient() {
        super(RequestSpecFactory.dummyJson());
    }

    @Step("Login with body: {body}")
    public Response login(Object body) {
        String bodyAsString = (body != null) ? body.toString() : "{}";

        // Send POST request
        Response res = post("/auth/login", body);

        return res;
    }

    @Step("Get /user/me with access token: {accessToken}")
    public Response userMe(String accessToken) {
        Map<String, String> headers = Map.of(
                "Authorization", "Bearer " + accessToken,
                "Accept-Encoding", "identity"
        );

        return get("/user/me", null, headers);
    }

    @Step("Search users with query: {q}")
    public Response searchUsers(String q) {
        Map<String, String> queryParam = Map.of("q", q);
        Response res = get("/users/search", queryParam, null);
        return res;
    }
}