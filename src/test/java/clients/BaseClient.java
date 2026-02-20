package clients;

import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import static io.restassured.RestAssured.given;

public abstract class BaseClient {

    private final RequestSpecification requestSpec;

    public BaseClient(RequestSpecification spec) {
        this.requestSpec = spec;
    }

    // Provide access to the spec for subclasses
    protected RequestSpecification getRequestSpec() {
        return requestSpec;
    }

    // Generic GET method
    public Response get(String path) {
        return given()
                .spec(requestSpec)
                .get(path);
    }
}