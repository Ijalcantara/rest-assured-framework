package utils;

import io.restassured.response.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AuthTokenUtil {

    private static final Logger log = LoggerFactory.getLogger(AuthTokenUtil.class);

    public static String getToken(Response response, String tokenPath) {
        String token = response.path(tokenPath);

        log.info("THREAD={} | TOKEN={}",
                Thread.currentThread().getName(),
                token);

        return token;
    }
}