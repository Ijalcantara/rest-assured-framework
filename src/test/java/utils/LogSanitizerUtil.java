package utils;

import java.util.HashMap;
import java.util.Map;

public class LogSanitizerUtil {

    private static final String MASK = "****";

    // Mask "password" key in a Map
    public static Map<String, Object> maskSensitive(Map<String, Object> data) {
        if (data == null) return new HashMap<>();
        Map<String, Object> masked = new HashMap<>(data);
        if (masked.containsKey("password")) {
            masked.put("password", MASK);
        }
        return masked;
    }

    // Generic string-based masking
    public static String maskSensitive(String input) {
        if (input == null || input.isBlank()) return "{}";
        return input.replaceAll("(?i)(\"?password\"?\\s*:\\s*\").*?\"", "$1" + MASK + "\"");
    }

    // For logging objects like Map safely
    public static String maskSensitiveObject(Object obj) {
        if (obj == null) return "{}";

        if (obj instanceof Map<?, ?> map) {
            Map<String, Object> masked = maskSensitive((Map<String, Object>) map);
            return masked.isEmpty() ? "{}" : masked.toString();
        }

        String str = obj.toString();
        return str.isEmpty() ? "{}" : maskSensitive(str);
    }
}