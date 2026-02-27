package constant;

public class ApiPaths {

    // Cloudflare endpoints
    public static final String CLOUDFLARE_BASE = "/client/v4/";

    // DummyJSON endpoints
    public static final String LOGIN = "/auth/login";
    public static final String USER_ME = "/user/me";
    public static final String SEARCH_USERS = "/users/search";

    // GoRest endpoints
    public static final String GOREST_USERS = "/users";
    public static final String GOREST_USER_BY_ID = "/users/{id}";

    // HttpBin endpoints
    public static final String HTTPBIN_STATUS = "/status/{code}";
    public static final String HTTPBIN_GET = "/get";
    public static final String HTTPBIN_DELAY = "/delay/{seconds}";

    private ApiPaths() {
        // prevent instantiation
    }
}