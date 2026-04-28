package manager;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

public final class TestDataManager {

    private static final Logger log = LoggerFactory.getLogger(TestDataManager.class);
    private static final ObjectMapper mapper = new ObjectMapper();
    private static JsonNode rootNode;
    private static String currentEnv;


    static {
        loadEnvironment(null);
    }

    private TestDataManager() {
    }

    /**
     * Loads testdata.json for the given environment.
     * If env is null or empty, tries system property, then ENV variable, then defaults to "dev".
     */
    public static void loadEnvironment(String env) {
        currentEnv = (env != null && !env.isBlank()) ? env : System.getProperty("env");

        if (currentEnv == null || currentEnv.isBlank()) {
            currentEnv = System.getenv("ENV");
        }

        if (currentEnv == null || currentEnv.isBlank()) {
            currentEnv = "dev"; // default fallback
        }

        InputStream is = TestDataManager.class.getClassLoader()
                .getResourceAsStream("testdata/" + currentEnv + "/testdata.json");

        if (is == null) {
            throw new RuntimeException("No testData.json found for environment: " + currentEnv);
        }

        log.info("Loading testData.json for environment: {}", currentEnv);

        try {
            rootNode = mapper.readTree(is);
        } catch (IOException e) {
            throw new RuntimeException("Failed to parse test data for environment: " + currentEnv, e);
        }
    }


    /** Returns a JsonNode for section/subsection/key, or empty object node if missing */
    public static JsonNode getDataNode(String section, String subsection, String key) {
        JsonNode node = rootNode.path(section).path(subsection).path(key);
        return node.isMissingNode() ? mapper.createObjectNode() : node;
    }

    /** Returns a Map<String, Object> safely, never null */
    public static Map<String, Object> getDataAsMap(String section, String subsection) {
        JsonNode node = rootNode.path(section).path(subsection);
        if (node.isMissingNode() || node.isNull()) {
            throw new RuntimeException(
                    "No test data found for section=" + section + ", subsection=" + subsection
            );
        }
        return mapper.convertValue(node, new TypeReference<Map<String, Object>>() {});
    }

    /** Returns nested map safely, never null */
    public static Map<String, Object> getNestedDataAsMap(String wrapper, String section, String subsection) {
        JsonNode node = rootNode.path(wrapper).path(section).path(subsection);
        if (node.isMissingNode() || node.isNull()) {
            throw new RuntimeException(
                    "No nested test data found for wrapper=" + wrapper +
                            ", section=" + section +
                            ", subsection=" + subsection
            );
        }
        return mapper.convertValue(node, new TypeReference<Map<String, Object>>() {});
    }
}