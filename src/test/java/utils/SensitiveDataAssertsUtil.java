package utils;

import static org.junit.jupiter.api.Assertions.assertFalse;
import java.util.HashMap;
import java.util.Map;

public class SensitiveDataAssertsUtil {

    // Mask the "password" field in a map
    public static Map<String, Object> maskPassword(Map<String, Object> data) {
        Map<String, Object> maskedData = new HashMap<>(data);
        if (maskedData.containsKey("password")) {
            maskedData.put("password", "****"); // mask it
        }
        return maskedData;
    }

    // Convert a map to string safely for logging
    public static String toLogString(Map<String, Object> data) {
        return maskPassword(data).toString();
    }

    // ✅ Add this back for assertions
    public static void assertDoesNotContainPassword(String responseBody) {
        assertFalse(responseBody.toLowerCase().contains("password"), "Response should not contain password");
    }
}