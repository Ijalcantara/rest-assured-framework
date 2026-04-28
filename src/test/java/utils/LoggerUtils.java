package utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class LoggerUtils {

    private LoggerUtils() {}

    public static Logger getLogger(Class<?> clazz) {
        return LoggerFactory.getLogger(clazz);
    }

    /**
     * Logs authentication context (login + token) in a clean format
     */
    public static void logUserToken(Logger log, String user, String token) {
        log.info("👤 USER  : {}", user);
        log.info("👷 WORKER: {}", Thread.currentThread().getName());
        log.info("🔑 TOKEN : {}", token);
        log.info("----------------------------------------");
    }
}