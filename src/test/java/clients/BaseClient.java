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
        this.requestSpec = requestSpec;
    }

    /**
     * Clone spec per request (parallel-safe)
     */
    protected RequestSpecification cloneSpec() {
        return new RequestSpecBuilder()
                .addRequestSpecification(requestSpec)
                .build();
    }

    /**
     * POST request
     */
    protected Response post(String path, Object body) {
        RequestSpecification specCopy = cloneSpec();
        Object maskedBody = LogSanitizerUtil.maskSensitiveObject(body);

        log.info("POST {}", path);
        log.info("Request Body: {}", maskedBody);

        Response res = given()
                .spec(specCopy)
                .body(body != null ? body : "{}")
                .when()
                .post(path);

        logResponseAndConsume(res); // ✅ fully consume response

        return res;
    }

    /**
     * GET request
     */
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
                .get(path);

        logResponseAndConsume(res);

        return res;
    }

    /**
     * Logs the response and fully consumes the body to release the connection
     */
    private void logResponseAndConsume(Response res) {
        if (res == null) {
            log.info("Response is null");
            return;
        }

        try {
            log.info("Response Status: {}", res.statusCode());
            log.info("Response Body: {}", LogSanitizerUtil.maskSensitiveObject(res.asPrettyString()));
            log.info("------------------------------------------------");

            // 🔑 Fully consume the body to release the connection
            res.getBody().asString();

        } catch (Exception e) {
            log.error("Failed to consume response body", e);
        }
    }
}