package utils;

import io.restassured.response.Response;
import org.junit.jupiter.api.Assumptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.UUID;

public class ApiTestUtils {

    private static final Logger log = LoggerFactory.getLogger(ApiTestUtils.class);

    public static String resolveToken() {
        String env = System.getenv("GOREST_TOKEN");
        if (env != null && !env.isBlank()) return env.trim();

        String cfg = config.ConfigManager.get("gorest.token");
        if (cfg != null && !cfg.isBlank()) return cfg.trim();

        throw new IllegalStateException("GoRest token not found. Set GOREST_TOKEN or config gorest.token.");
    }

    public static boolean looksLikeCloudflare(Response res) {
        String ct = res.getHeader("content-type");
        String body = res.asString();

        if (ct == null) ct = "";
        ct = ct.toLowerCase();

        return ct.contains("text/html")
                && body.contains("Just a moment")
                && (body.contains("challenge-platform") || body.contains("_cf_chl_opt"));
    }

    public static void assumeNotCloudflare(Response res) {
        Assumptions.assumeFalse(
                looksLikeCloudflare(res),
                "Blocked by Cloudflare challenge. Use a self-hosted runner if needed."
        );
    }

    // --- Reusable method for unique email with logging ---
    public static Map<String, Object> makeUniqueEmail(Map<String, Object> payload, String key) {
        if (payload.containsKey(key)) {
            String original = (String) payload.get(key);
            String[] parts = original.split("@");
            String unique = parts[0] + "+" + UUID.randomUUID().toString().substring(0, 5) + "@" + parts[1];
            payload.put(key, unique);
            log.info("Updated '{}' with unique value: {}", key, unique);
        } else {
            log.warn("Payload does not contain key '{}', cannot make unique", key);
        }
        return payload;
    }
}