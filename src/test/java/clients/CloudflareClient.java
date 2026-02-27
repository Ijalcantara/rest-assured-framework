package clients;

import constant.ApiPaths;
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
        return get(ApiPaths.CLOUDFLARE_BASE, null, null);
    }

    @Step("Call Cloudflare using HTTP without following redirect")
    public Response callUsingHttpWithoutRedirect() {
        var spec = cloneSpec();

        return given()
                .spec(spec)
                .redirects().follow(false)
                .when()
                .get(ApiPaths.CLOUDFLARE_BASE)
                .then()
                .extract()
                .response();
    }
}