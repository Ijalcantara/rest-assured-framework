package utils.reusablemethod;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import io.qameta.allure.Allure;
import io.restassured.response.Response;
import org.junit.jupiter.api.Assertions;
import utils.LogSanitizerUtil;
import utils.SensitiveDataAssertsUtil;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;


public class ReusableMethod {

    private static final ObjectMapper mapper = new ObjectMapper()
            .enable(SerializationFeature.INDENT_OUTPUT);

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

    /**
     * Attach API call in a single JSON attachment
     * Includes request payload, status code, response time (if real Response), and response body
     * Use this for ALL tests (real or mock) to keep Allure uniform
     */
    public static void attachApiWithMockResponse(Map<String, Object> requestPayload, Object statusOrResponse, Object responseBody) {
        try {
            int statusCode;
            long responseTimeMs = -1;

            if (statusOrResponse instanceof Response res) {
                statusCode = res.statusCode();
                responseTimeMs = res.time();
                if (responseBody == null) {
                    responseBody = res.getBody().asString();
                }
            } else {
                // If manual/mock response
                statusCode = (int) statusOrResponse;
            }

            Map<String, Object> summary = Map.of(
                    "requestPayload", requestPayload != null ? requestPayload : Map.of(),
                    "statusCode", statusCode,
                    "responseTimeMs", responseTimeMs,
                    "responseBody", responseBody != null ? responseBody : Map.of()
            );

            String prettyJson = toPrettyJson(summary);

            Allure.addAttachment(
                    "API Request / Response",
                    "application/json",
                    new ByteArrayInputStream(prettyJson.getBytes(StandardCharsets.UTF_8)),
                    ".json"
            );

        } catch (Exception e) {
            Allure.addAttachment("API Request / Response", "Failed to attach unified API call: " + e.getMessage());
        }
    }


    public static void validateApiScenario(
            String scenario,
            Map<String, Object> requestPayload,
            Response response,
            int expectedStatusCode,
            String... requiredResponseFields) {

        // 1️⃣ Business Scenario
        Allure.step("Scenario: " + scenario);

        // 2️⃣ Validation of Request Payload (mask password)
        Allure.step("Validation of Request Payload", () -> {
            String safePayload = LogSanitizerUtil.maskSensitiveObject(requestPayload);
            Allure.step(safePayload);
        });

        // 3️⃣ Validation of Status Code
        Allure.step("Validation of Status Code", () -> {
            int actualStatus = response.statusCode();
            Allure.step("Returned Status Code: " + actualStatus);
            Assertions.assertEquals(expectedStatusCode, actualStatus,
                    "Status code validation failed");
        });

        // 4️⃣ Validation of Response Body (mask sensitive fields)
        Allure.step("Validation of Response Body", () -> {
            String body = response.getBody().asPrettyString();
            String safeBody = LogSanitizerUtil.maskSensitive(body);
            Allure.step(safeBody);

            if (requiredResponseFields != null) {
                for (String field : requiredResponseFields) {
                    Assertions.assertNotNull(
                            response.jsonPath().get(field),
                            "Missing required field: " + field
                    );
                }
            }
        });
    }
}