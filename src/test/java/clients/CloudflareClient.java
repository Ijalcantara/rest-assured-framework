package clients;

import core.RequestSpecFactory;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

public class CloudflareClient extends BaseClient {

    public CloudflareClient() {
        super(RequestSpecFactory.cloudflare());
    }

    public Response callUsingHttp() {
        return get("/client/v4/");
    }

    public Response callUsingHttpWithoutRedirect() {
        return given()
                .baseUri("http://your.cloudflare.url")
                .redirects().follow(false) // don't follow redirect to HTTPS
                .get("/some-path")
                .then()
                .extract()
                .response();
    }
}