package clients;

import constant.EndpointConstant;
import core.RequestSpecFactory;
import io.qameta.allure.Step;
import io.restassured.response.Response;

import java.util.Map;

public class DummyJsonClient extends BaseClient {

    public DummyJsonClient() {
        super(RequestSpecFactory.dummyJson());
    }

    @Step("Login with body: {body}")
    public Response login(Object body) {
        // Default to empty JSON if null
        String bodyAsString = (body != null) ? body.toString() : "{}";

        // Send POST request using constant path
        return post(EndpointConstant.LOGIN, body);
    }

    @Step("Get /user/me with access token: {accessToken}")
    public Response userMe(String accessToken) {
        Map<String, String> headers = Map.of(
                "Authorization", "Bearer " + accessToken,
                "Accept-Encoding", "identity"
        );

        return get(EndpointConstant.USER_ME, null, headers);
    }

    @Step("Search users with query: {q}")
    public Response searchUsers(String q) {
        Map<String, String> queryParam = Map.of("q", q);
        return get(EndpointConstant.SEARCH_USERS, queryParam, null);
    }
}