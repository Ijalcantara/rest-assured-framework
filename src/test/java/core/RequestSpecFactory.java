package core;

import config.ConfigManager;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.config.HttpClientConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.filter.log.LogDetail;
import io.restassured.specification.RequestSpecification;

import static io.restassured.RestAssured.config;
import static io.restassured.http.ContentType.JSON;

public final class RequestSpecFactory {

    private RequestSpecFactory() {}

    /**
     * Builds a new RequestSpecification per call (parallel-safe)
     */
    private static RequestSpecification buildSpec(String baseUrl, boolean withJson) {
        int connectionTimeout = ConfigManager.getInt("timeout.connection");
        int socketTimeout = ConfigManager.getInt("timeout.socket");

        RestAssuredConfig restConfig = config()
                .httpClient(HttpClientConfig.httpClientConfig()
                        .setParam("http.connection.timeout", connectionTimeout)
                        .setParam("http.socket.timeout", socketTimeout)
                        .setParam("http.connection-manager.timeout", connectionTimeout)
                        .reuseHttpClientInstance()
                );

        RequestSpecBuilder builder = new RequestSpecBuilder()
                .setBaseUri(baseUrl)
                .setConfig(restConfig)
                .log(LogDetail.URI);

        if (withJson) {
            builder.setContentType(JSON)
                    .setAccept(JSON);
        }

        return builder.build();
    }

    public static RequestSpecification dummyJson() {
        return buildSpec(ConfigManager.get("base.url.dummyjson"), true);
    }

    public static RequestSpecification contactList() {
        return buildSpec(ConfigManager.get("base.url.contactlist"), true);
    }
}