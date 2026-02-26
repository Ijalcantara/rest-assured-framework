package utils.reusablemethod;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import io.qameta.allure.Allure;
import io.restassured.response.Response;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
     * Assertion helper for login responses
     */
    public static void assertLoginResponse(Response res, int expectedStatus, boolean expectToken) {
        assert res != null;
        assertEquals(expectedStatus, res.statusCode(), "Unexpected status code");

        String accessToken = res.jsonPath().getString("accessToken");
        String refreshToken = res.jsonPath().getString("refreshToken");
        String message = res.jsonPath().getString("message");

        if (expectToken) {
            assert accessToken != null && !accessToken.isEmpty() : "AccessToken should not be null/empty";
            assert refreshToken != null && !refreshToken.isEmpty() : "RefreshToken should not be null/empty";
        } else {
            assert accessToken == null : "AccessToken should be null";
            assert refreshToken == null : "RefreshToken should be null";
        }

        if (message != null) {
            assert !message.isEmpty() : "Message should not be empty if present";
        }
    }

    /**
     * Attach API call in a single JSON attachment
     * Includes request payload, status code, response time (if real Response), and response body
     * Use this for ALL tests (real or mock) to keep Allure uniform
     */
    public static void attachApiCallUnified(Map<String, Object> requestPayload, Object statusOrResponse, Object responseBody) {
        try {
            int statusCode;
            long responseTimeMs = -1;

            // If real Response
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

    /**
     * Validate that a response field matches the expected value
    /**
     * Validate multiple fields in the response match expected values
     */
    public static void validateResponseFields(Response res, Map<String, Object> expectedFields) {
        Allure.step("Validate response fields match expected values", () -> {
            expectedFields.forEach((field, expectedValue) -> {
                Object actualValue = res.jsonPath().get(field);
                assertEquals(expectedValue, actualValue,
                        "Expected field '" + field + "' to be '" + expectedValue + "' but was '" + actualValue + "'");
            });
        });
    }

    public static void attachBusinessSummary(
            String scenario,
            String expectedBehavior,
            Response res
    ) {
        String actualResult;
        int status = res.statusCode();

        if (status >= 200 && status < 300) {
            actualResult = "System accepted the request.";
        } else if (status >= 400 && status < 500) {
            actualResult = "System rejected the invalid input.";
        } else if (status >= 500) {
            actualResult = "System encountered a server error.";
        } else {
            actualResult = "Unexpected response received.";
        }

        // Attach each part as its own step
        Allure.step("Scenario: " + scenario);
        Allure.step("Expected Behavior: " + expectedBehavior);
        Allure.step("Actual Result: " + actualResult);
    }

    private static final Map<String, Map<String, Object>> expectedFieldsStore = new HashMap<>();

    /**
     * Store expected fields for a given key.
     * @param key unique identifier for the test or endpoint
     * @param fields map of expected fields
     */
    public static void storeExpectedFields(String key, Map<String, Object> fields) {
        expectedFieldsStore.put(key, fields);
    }

    /**
     * Retrieve previously stored expected fields for a given key.
     * @param key unique identifier for the test or endpoint
     * @return map of expected fields
     */
    public static Map<String, Object> getExpectedFields(String key) {
        return expectedFieldsStore.get(key);
    }
}