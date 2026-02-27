package tests.resiliency;

import clients.CloudflareClient;
import core.BaseApiTest;
import io.qameta.allure.*;
import io.restassured.response.Response;
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.reusablemethod.ReusableMethod;

@Epic("Cloudflare API")
@Feature("Protocol Validation")
@DisplayName("CloudflareProtocolTests")
public class CloudflareProtocolTests extends BaseApiTest {

    private static final Logger log = LoggerFactory.getLogger(CloudflareProtocolTests.class);

    @Test
    @Tag("negative")
    @DisplayName("TC17 - HTTP request should fail instead of HTTPS")
    void Test17_should_fail_when_using_http_instead_of_https() {

        CloudflareClient client = new CloudflareClient();

        try {
            Response res = client.callUsingHttpWithoutRedirect();

            ReusableMethod.validateApiScenario(
                    "Attempt to call Cloudflare endpoint using HTTP instead of HTTPS.",
                    null,
                    res,
                    400 // expected failure code
            );
            ReusableMethod.attachApiCall(null, res);

        } catch (Exception e) {
            log.info("Expected exception occurred: {}", e.getMessage());
        }
    }
}

//package tests.resiliency;
//
//import clients.CloudflareClient;
//import core.BaseApiTest;
//import io.qameta.allure.Epic;
//import io.qameta.allure.Feature;
//import io.qameta.allure.Story;
//import io.restassured.response.Response;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Tag;
//import org.junit.jupiter.api.Test;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import utils.reusablemethod.ReusableMethod;
//
//import java.util.Map;
//
//@Epic("Cloudflare API")
//@Feature("Protocol Validation")
//@DisplayName("CloudflareProtocolTests")
//public class CloudflareProtocolTests extends BaseApiTest {
//
//    private static final Logger log = LoggerFactory.getLogger(CloudflareProtocolTests.class);
//
//    @Test
//    @Tag("negative")
//    @DisplayName("TC17 - HTTP request should fail instead of HTTPS")
//    @Story("HTTP request should fail when HTTPS is required")
//    void Test17_should_fail_when_using_http_instead_of_https() {
//
//        CloudflareClient client = new CloudflareClient();
//
//        try {
//            Response res = client.callUsingHttpWithoutRedirect();
//            ReusableMethod.attachApiCall(null, res);
//            ReusableMethod.attachBusinessSummary(
//                    "Attempt to call Cloudflare endpoint using HTTP instead of HTTPS."
//            );
//        } catch (Exception e) {
//            log.info("Expected exception occurred: {}", e.getMessage());
//        }
//    }
//}