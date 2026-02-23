package clients;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import static io.restassured.RestAssured.given;

public abstract class BaseClient {

    private final RequestSpecification requestSpec;

    public BaseClient(RequestSpecification spec) {
        // Attach Allure filter to the spec (already done in RequestSpecFactory)
        this.requestSpec = spec;
    }

    protected RequestSpecification getRequestSpec() {
        return requestSpec;
    }

    @Step("GET request to {path}")
    public Response get(String path) {
        return given()
                .spec(requestSpec)
                .get(path);
    }
}