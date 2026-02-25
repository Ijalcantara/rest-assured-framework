package clients;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.specification.RequestSpecification;
import io.restassured.response.Response;

import java.util.Map;

import static io.restassured.RestAssured.given;

public abstract class BaseClient {

    protected final RequestSpecification requestSpec;

    public BaseClient(RequestSpecification requestSpec) {
        // Keep spec immutable
        this.requestSpec = requestSpec;
    }

    /**
     * Clone the requestSpec per request to avoid shared mutable state (parallel-safe)
     */
    protected RequestSpecification cloneSpec() {
        return new RequestSpecBuilder()
                .addRequestSpecification(requestSpec)
                .build();
    }

    /**
     * Thread-safe POST
     */
    protected Response post(String path, Object body) {
        RequestSpecification specCopy = cloneSpec();

        // Print request info to console
        System.out.println("POST " + path);
        if (body != null) System.out.println("Request Body: " + body);
        else System.out.println("Request Body: {}");

        Response res = given()
                .spec(specCopy)
                .body(body != null ? body : "{}")
                .when()
                .post(path)
                .then()
                .extract()
                .response();

        // Print response info to console
        System.out.println("Response Status: " + res.statusCode());
        System.out.println("Response Body: " + res.asPrettyString());
        System.out.println("------------------------------------------------");

        return res;
    }
    /**
     * Thread-safe GET
     */
    protected Response get(String path, Map<String, ?> queryParams, Map<String, ?> headers) {
        RequestSpecification specCopy = cloneSpec();

        if (headers != null) headers.forEach(specCopy::header);
        if (queryParams != null) specCopy.queryParams(queryParams);

        System.out.println("GET " + path);
        if (queryParams != null) System.out.println("Query Params: " + queryParams);
        if (headers != null) System.out.println("Headers: " + headers);

        Response res = given()
                .spec(specCopy)
                .when()
                .get(path)
                .then()
                .extract()
                .response();

        System.out.println("Response Status: " + res.statusCode());
        System.out.println("Response Body: " + res.asPrettyString());
        System.out.println("------------------------------------------------");

        return res;
    }
}