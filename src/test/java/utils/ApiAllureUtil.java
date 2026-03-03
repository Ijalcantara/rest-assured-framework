package utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import io.qameta.allure.Allure;
import io.restassured.response.Response;
import org.junit.jupiter.api.Assertions;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;


public class ApiAllureUtil {

    private static final ObjectMapper mapper = new ObjectMapper()
            .enable(SerializationFeature.INDENT_OUTPUT);

    public static void logScenario(String scenario) {
        Allure.step("Scenario: " + scenario);
    }

    public static void logRequestPayload(Map<String, Object> requestPayload) {
        Allure.step("Validation of Request Payload:", () -> {
            String maskedPayload = LogSanitizerUtil.maskSensitiveObject(requestPayload);
            Allure.step(maskedPayload);
        });
    }

    public static void validateStatusCode(Response response, int expectedStatusCode) {
        Allure.step("Validation of Status Code", () -> {
            int actualStatus = response == null ? -1 : response.statusCode();
            Allure.step("Returned Status Code: " + actualStatus);

            Assertions.assertEquals(
                    expectedStatusCode,
                    actualStatus,
                    "Status code validation failed"
            );
        });
    }

    public static void validateResponseBody(
            Response response,
            String... requiredResponseFields) {

        Allure.step("Validation of Response Body:", () -> {

            String body = response == null
                    ? "{}"
                    : response.getBody().asPrettyString();

            Allure.step(LogSanitizerUtil.maskSensitive(body));

            if (requiredResponseFields != null && response != null) {
                for (String field : requiredResponseFields) {
                    Assertions.assertNotNull(
                            response.jsonPath().get(field),
                            "Missing required field: " + field
                    );
                }
            }
        });
    }

    /**
     * Attach a single uniform API call summary to Allure
     * Includes request payload, status code, response time, and response body
     */
    public static void attachApiCall(Map<String, Object> requestPayload, Response res) {
        try {
            String jsonSummary = "{\n" +
                    "  \"requestPayload\": " + toPrettyJson(requestPayload) + ",\n" +
                    "  \"statusCode\": " + res.statusCode() + ",\n" +
                    "  \"responseTimeMs\": " + res.time() + ",\n" +
                    "  \"responseBody\": " + toPrettyJson(res.getBody().asString()) + "\n" +
                    "}";

            Allure.addAttachment("API Request / Response",
                    "application/json",
                    new ByteArrayInputStream(jsonSummary.getBytes(StandardCharsets.UTF_8)),
                    ".json");

        } catch (Exception e) {
            Allure.addAttachment("API Request / Response", "Failed to attach: " + e.getMessage());
        }
    }
    /**
     * Pretty-print an object or JSON string
     */
    private static String toPrettyJson(Object obj) {
        try {
            if (obj instanceof String str) {
                // Try to parse as JSON
                return mapper.writerWithDefaultPrettyPrinter()
                        .writeValueAsString(mapper.readTree(str));
            }
            // Map or POJO
            return mapper.writerWithDefaultPrettyPrinter()
                    .writeValueAsString(obj);
        } catch (Exception e) {
            return obj.toString(); // fallback
        }
    }
}