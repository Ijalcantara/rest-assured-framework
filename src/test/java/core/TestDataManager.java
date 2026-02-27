package core;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

public class TestDataManager {

    private static final Logger log = LoggerFactory.getLogger(TestDataManager.class);
    private static final ObjectMapper mapper = new ObjectMapper();
    private static JsonNode rootNode;

    static {
        String env = System.getenv("ENV");
        if (env == null || env.isBlank()) {
            env = "dev"; // default environment
        }

        log.info("Loading testData.json for environment: {}", env);

        // Load from classpath resources
        try (InputStream is = TestDataManager.class.getClassLoader()
                .getResourceAsStream("testdata/" + env + "/testdata.json")) {

            if (is == null) {
                throw new RuntimeException("testData.json not found in resources for environment: " + env);
            }

            rootNode = mapper.readTree(is);

        } catch (IOException e) {
            throw new RuntimeException("Failed to load test data for environment: " + env, e);
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