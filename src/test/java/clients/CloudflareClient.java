package clients;

import core.RequestSpecFactory;
import io.qameta.allure.Step;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

public class CloudflareClient extends BaseClient {

    public CloudflareClient() {
        super(RequestSpecFactory.cloudflare());
    }

    @Step("Call Cloudflare base endpoint")
    public Response callUsingHttp() {
        return get("/client/v4/", null, null);
    }

    @Step("Call Cloudflare using HTTP without following redirect")
    public Response callUsingHttpWithoutRedirect() {
        // Clone spec from BaseClient
        var spec = cloneSpec();

        // Use given() to apply redirect override safely
        Response res = given()
                .spec(spec)
                .redirects().follow(false)   // do not follow redirects
                .when()
                .get("/client/v4/")          // endpoint
                .then()
                .extract()
                .response();

        return res;
    }
}