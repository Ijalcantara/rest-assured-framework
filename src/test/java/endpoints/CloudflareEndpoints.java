package endpoints;

public final class CloudflareEndpoints {

    private CloudflareEndpoints() {}

    // Base path for Cloudflare v4 API
    public static final String BASE_V4 = "/client/v4";

    // Example "base endpoint" you were calling
    public static final String ROOT = BASE_V4 + "/";

    // If you add real Cloudflare endpoints later, you can extend like:
    // public static final String ZONES = BASE_V4 + "/zones";
    // public static final String ZONE_DETAILS = BASE_V4 + "/zones/{zone_id}";
}