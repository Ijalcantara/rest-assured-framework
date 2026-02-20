package core;

import config.ConfigManager;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.config.HttpClientConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.filter.log.LogDetail;
import io.restassured.specification.RequestSpecification;

import static io.restassured.RestAssured.config;
import static io.restassured.http.ContentType.JSON;

public final class RequestSpecFactory {

    private static final AllureRestAssured ALLURE_FILTER =
            new AllureRestAssured();

    private RequestSpecFactory() {}

    private static RequestSpecification buildSpec(String baseUrl, boolean withJson) {

        int connectionTimeout = ConfigManager.getInt("timeout.connection");
        int socketTimeout = ConfigManager.getInt("timeout.socket");

        RestAssuredConfig restConfig = config()
                .httpClient(HttpClientConfig.httpClientConfig()
                        .setParam("http.connection.timeout", connectionTimeout)
                        .setParam("http.socket.timeout", socketTimeout));

        RequestSpecBuilder builder = new RequestSpecBuilder()
                .setBaseUri(baseUrl)
                .setConfig(restConfig)
                .addFilter(ALLURE_FILTER)
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

    public static RequestSpecification httpBin() {
        return buildSpec(ConfigManager.get("base.url.httpbin"), false);
    }

    public static RequestSpecification advantage() {
        return buildSpec(ConfigManager.get("base.url.advantage"), true);
    }

    public static RequestSpecification gorest() {
        return buildSpec(ConfigManager.get("base.url.gorest"), true);
    }

    public static RequestSpecification cloudflare() {
        return buildSpec(ConfigManager.get("base.url.cloudflare"), false);
    }
}