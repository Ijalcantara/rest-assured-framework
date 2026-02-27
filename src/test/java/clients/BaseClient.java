package clients;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.specification.RequestSpecification;
import io.restassured.response.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.LogSanitizerUtil;

import java.util.Map;

import static io.restassured.RestAssured.given;

public abstract class BaseClient {

    protected final RequestSpecification requestSpec;
    private static final Logger log = LoggerFactory.getLogger(BaseClient.class);

    public BaseClient(RequestSpecification requestSpec) {
        // Keep spec immutable
        this.requestSpec = requestSpec;
    }

    /** Clone the requestSpec per request to avoid shared mutable state (parallel-safe) */
    protected RequestSpecification cloneSpec() {
        return new RequestSpecBuilder()
                .addRequestSpecification(requestSpec)
                .build();
    }

    /** Thread-safe POST with masked logging */
    protected Response post(String path, Object body) {
        RequestSpecification specCopy = cloneSpec();

        // Mask request body if it's a Map or String
        Object maskedBody = LogSanitizerUtil.maskSensitiveObject(body);

        log.info("POST {}", path);
        log.info("Request Body: {}", maskedBody);

        Response res = given()
                .spec(specCopy)
                .body(body != null ? body : "{}")
                .when()
                .post(path)
                .then()
                .extract()
                .response();

        logResponse(res);
        return res;
    }

    /** Thread-safe GET with masked logging */
    protected Response get(String path, Map<String, ?> queryParams, Map<String, ?> headers) {
        RequestSpecification specCopy = cloneSpec();

        if (headers != null) headers.forEach(specCopy::header);
        if (queryParams != null) specCopy.queryParams(queryParams);

        log.info("GET {}", path);
        if (queryParams != null) log.info("Query Params: {}", queryParams);
        if (headers != null) log.info("Headers: {}", headers);

        Response res = given()
                .spec(specCopy)
                .when()
                .get(path)
                .then()
                .extract()
                .response();

        logResponse(res);
        return res;
    }

    /** Logs response with masking for sensitive fields */
    private void logResponse(Response res) {
        if (res == null) {
            log.info("Response is null");
            return;
        }
        log.info("Response Status: {}", res.statusCode());
        log.info("Response Body: {}", LogSanitizerUtil.maskSensitiveObject(res.asPrettyString()));
        log.info("------------------------------------------------");
    }
}