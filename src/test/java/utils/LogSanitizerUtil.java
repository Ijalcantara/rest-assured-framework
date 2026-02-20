package utils;

import java.util.HashMap;
import java.util.Map;

public class LogSanitizerUtil {

    private static final String MASK = "****";

    // Mask "password" key in a Map
    public static Map<String, Object> maskSensitive(Map<String, Object> data) {
        Map<String, Object> masked = new HashMap<>(data);
        if (masked.containsKey("password")) {
            masked.put("password", MASK);
        }
        return masked;
    }

    // Generic string-based masking
    public static String maskSensitive(String input) {
        if (input == null) return null;
        return input.replaceAll("(?i)(\"?password\"?\\s*:\\s*\").*?\"", "$1" + MASK + "\"");
    }

    // For logging objects like Map
    public static String maskSensitiveObject(Object obj) {
        if (obj instanceof Map<?, ?> map) {
            return maskSensitive((Map<String, Object>) map).toString();
        }
        return maskSensitive(obj.toString());
    }
}