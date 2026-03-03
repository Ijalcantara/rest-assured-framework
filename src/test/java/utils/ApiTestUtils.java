package utils;

import io.restassured.response.Response;
import org.junit.jupiter.api.Assumptions;

public class ApiTestUtils {

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
}