package manager;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TestDataManager {

    private static final Logger log = LoggerFactory.getLogger(TestDataManager.class);
    private static final ObjectMapper mapper = new ObjectMapper();
    private static JsonNode rootNode;
    private static String currentEnv;

    static {
        loadEnvironment(null);
    }

    /**
     * Load environment test data
     */
    public static void loadEnvironment(String env) {
        // Check -Denv JVM property first
        currentEnv = (env != null && !env.isBlank()) ? env : System.getProperty("env");

        // fallback to ENV environment variable
        if (currentEnv == null || currentEnv.isBlank()) {
            currentEnv = System.getenv("ENV");
        }

        // fallback to dev
        if (currentEnv == null || currentEnv.isBlank()) {
            currentEnv = "dev";
        }

        // Attempt to load the requested environment
        InputStream is = TestDataManager.class.getClassLoader()
                .getResourceAsStream("testdata/" + currentEnv + "/testdata.json");

        // fallback to first available environment if missing
        if (is == null) {
            List<String> availableEnvs = listAvailableEnvironments();
            if (!availableEnvs.isEmpty()) {
                String fallbackEnv = availableEnvs.get(0);
                log.warn("Environment '{}' not found. Falling back to '{}'", currentEnv, fallbackEnv);
                currentEnv = fallbackEnv;
                is = TestDataManager.class.getClassLoader()
                        .getResourceAsStream("testdata/" + currentEnv + "/testdata.json");
            }
        }

        if (is == null) {
            throw new RuntimeException("No testData.json found for environment: " + currentEnv);
        }

        log.info("Loading testData.json for environment: {}", currentEnv);

        try {
            rootNode = mapper.readTree(is);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load test data for environment: " + currentEnv, e);
        }
    }

    /**
     * List all environment folders under resources/testdata
     */
    private static List<String> listAvailableEnvironments() {
        try {
            return Stream.of(Objects.requireNonNull(
                            TestDataManager.class.getClassLoader().getResources("testdata")
                    ))
                    .flatMap(urls -> urls.hasMoreElements() ? Stream.of(urls.nextElement()) : Stream.empty())
                    .map(url -> new java.io.File(url.getPath()))
                    .flatMap(file -> Stream.of(Objects.requireNonNull(file.list())))
                    .collect(Collectors.toList());
        } catch (IOException e) {
            log.warn("Failed to list available environments", e);
            return List.of();
        }
    }

    /**
     * Get a specific key as JsonNode
     */
    public static JsonNode getDataNode(String section, String subsection, String key) {
        return rootNode.path(section).path(subsection).path(key);
    }

    /**
     * 2-argument version: subsection as a Map
     */
    public static Map<String, Object> getDataAsMap(String section, String subsection) {
        return mapper.convertValue(
                rootNode.path(section).path(subsection),
                new TypeReference<Map<String, Object>>() {}
        );
    }

    /**
     * 3-argument legacy method for backward compatibility
     * The first argument is ignored (e.g., wrapper or file)
     */
    public static Map<String, Object> getNestedDataAsMap(String wrapper, String section, String subsection) {
        return mapper.convertValue(
                rootNode.path(wrapper).path(section).path(subsection),
                new TypeReference<Map<String, Object>>() {}
        );
    }
}
