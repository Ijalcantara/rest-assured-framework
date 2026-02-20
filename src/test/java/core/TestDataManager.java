package core;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.response.Response;
import clients.DummyJsonClient;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import static utils.reusablemethod.ReusableMethod.*;

public class TestDataManager {

    private static final ObjectMapper mapper = new ObjectMapper();
    private static JsonNode rootNode;
    private static final DummyJsonClient api = new DummyJsonClient();


    static {
        try {
            File file = new File("src/test/resources/testdata/testdata.json");
            rootNode = mapper.readTree(file);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load test data JSON", e);
        }
    }

    // Generic method to get any node as Map
    public static Map<String, Object> getDataAsMap(String... path) {
        JsonNode node = rootNode;
        for (String p : path) {
            node = node.path(p);
        }
        return mapper.convertValue(node, new TypeReference<>() {});
    }

    // Optionally, get JsonNode directly
    public static JsonNode getDataNode(String... path) {
        JsonNode node = rootNode;
        for (String p : path) {
            node = node.path(p);
        }
        return node;
    }

    // Option 1: fetch login map directly from TestDataManager without using getDummyLogin
    public static String getToken(String loginKey) {
        logTestStart("Generate Access Token");

        // Directly get the login map
        Map<String, Object> loginUser = getDataAsMap("dummyjson", "login", loginKey);

       // log.info("Logging in user: {}", loginUser.get("username"));

        Response res = api.login(loginUser);

        logResponse(res);
        assertLoginResponse(res, 200, true);

        String token = res.jsonPath().getString("accessToken");
       // log.info("Access token generated successfully.");

        logTestEnd("Generate Access Token");
        return token;
    }
}