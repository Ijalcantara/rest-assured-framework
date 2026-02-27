package clients;

import core.RequestSpecFactory;
import endpoints.CloudflareEndpoints;
import io.qameta.allure.Step;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

public class CloudflareClient extends BaseClient {

    public CloudflareClient() {
        super(RequestSpecFactory.cloudflare());
    }

    @Step("Call Cloudflare base endpoint")
    public Response callUsingHttp() {
        // uses BaseClient.get(String)
        return get(CloudflareEndpoints.ROOT);
    }

    @Step("Call Cloudflare using HTTP without following redirect")
    public Response callUsingHttpWithoutRedirect() {
        // reuse BaseClient's request spec and override redirect behavior
        return given()
                .spec(getRequestSpec())
                .redirects().follow(false)
                .when()
                .get(CloudflareEndpoints.ROOT)
                .then()
                .extract()
                .response();
    }
}