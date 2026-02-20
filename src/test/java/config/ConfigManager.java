package config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.Properties;

public final class ConfigManager {

    private static final Logger log = LoggerFactory.getLogger(ConfigManager.class);
    private static final Properties props = new Properties();
    private static final String ENV;

    static {
        try {
            ENV = System.getProperty("env", "qa").trim().toLowerCase();
            String fileName = "config/" + ENV + ".properties";

            try (InputStream input =
                         ConfigManager.class.getClassLoader().getResourceAsStream(fileName)) {

                if (input == null)
                    throw new IllegalStateException("Environment file not found: " + fileName);

                props.load(input);
            }

            log.info("Config loaded: env={}", ENV);

        } catch (Exception e) {
            throw new RuntimeException("Failed to load config", e);
        }
    }

    private ConfigManager() {}

    public static String env() {
        return ENV;
    }

    public static String get(String key) {
        String v = System.getProperty(key);
        if (v != null && !v.isBlank()) return v.trim();

        v = props.getProperty(key);
        if (v == null || v.isBlank()) {
            throw new IllegalArgumentException("Missing config key: " + key + " (env=" + ENV + ")");
        }
        return v.trim();
    }

    // ✅ Needed for timeouts
    public static int getInt(String key) {
        return Integer.parseInt(get(key));
    }

    // ✅ Useful for enabling/disabling features
    public static boolean getBoolean(String key) {
        return Boolean.parseBoolean(get(key));
    }
}