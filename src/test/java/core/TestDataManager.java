package core;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.Map;

public class TestDataManager {

    private static final ObjectMapper mapper = new ObjectMapper();
    private static JsonNode rootNode;

    static {
        try {
            File file = new File("src/test/resources/testdata/testdata.json");
            rootNode = mapper.readTree(file);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load test data JSON", e);
        }
    }

    /**
     * Generic method to get any node as Map
     */
    public static Map<String, Object> getDataAsMap(String... path) {
        JsonNode node = rootNode;
        for (String p : path) {
            node = node.path(p);
        }
        return mapper.convertValue(node, new TypeReference<>() {});
    }

    /**
     * Optionally, get JsonNode directly
     */
    public static JsonNode getDataNode(String... path) {
        JsonNode node = rootNode;
        for (String p : path) {
            node = node.path(p);
        }
        return node;
    }

}