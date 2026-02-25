package utils.reusablemethod;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import io.qameta.allure.Allure;
import io.restassured.response.Response;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
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
     * Attach raw JSON response (optional, for individual response attachments)
     */
    public static void attachJsonResponse(String name, Response response) {
        if (response != null) {
            try {
                String prettyJson = mapper.writerWithDefaultPrettyPrinter()
                        .writeValueAsString(mapper.readTree(response.getBody().asString()));

                Allure.addAttachment(
                        name,
                        "application/json",
                        new ByteArrayInputStream(prettyJson.getBytes(StandardCharsets.UTF_8)),
                        ".json"
                );
            } catch (Exception e) {
                Allure.addAttachment(name, response.getBody().asString());
            }
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
}