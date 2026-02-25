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
        return get("/status/" + code, null, null);
    }

    public Response getCall() {
        return get("/get", null, null);
    }

    public Response delay(int seconds) {
        return get("/delay/" + seconds, null, null);
    }

    // ---------------------------
    // Delay endpoint with custom timeout
    // ---------------------------
    public Response delayWithTimeout(int seconds, int timeoutMs) {
        var customSpec = new io.restassured.builder.RequestSpecBuilder()
                .addRequestSpecification(requestSpec) // copy base spec
                .setConfig(RestAssuredConfig.config()
                        .httpClient(HttpClientConfig.httpClientConfig()
                                .setParam("http.connection.timeout", timeoutMs)
                                .setParam("http.socket.timeout", timeoutMs)))
                .build();

        return given()
                .spec(customSpec)
                .get("/delay/" + seconds)
                .then()
                .log().ifValidationFails()
                .extract()
                .response();
    }
}