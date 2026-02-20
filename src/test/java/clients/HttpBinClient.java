package clients;

import core.RequestSpecFactory;
import io.restassured.config.HttpClientConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

public class HttpBinClient extends BaseClient {

    public HttpBinClient() {
        super(RequestSpecFactory.httpBin());
    }

    public Response status(int code) {
        return get("/status/" + code);
    }

    public Response getCall() {
        return get("/get");
    }

    public Response delay(int seconds) {
        return get("/delay/" + seconds);
    }

    public Response delayWithTimeout(int seconds, int timeoutMs) {
        RestAssuredConfig customConfig = RestAssuredConfig.config()
                .httpClient(HttpClientConfig.httpClientConfig()
                        .setParam("http.connection.timeout", timeoutMs)
                        .setParam("http.socket.timeout", timeoutMs));

        return given()
                .spec(getRequestSpec().config(customConfig))
                .get("/delay/" + seconds);
    }
}