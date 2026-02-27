package core;

import io.restassured.RestAssured;
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public abstract class BaseApiTest {

    private static final Logger log = LoggerFactory.getLogger(BaseApiTest.class);
    private static long suiteStartTime;

    @BeforeAll
    public static void globalSetup() {
        suiteStartTime = System.currentTimeMillis();
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    @BeforeEach
    void beforeEachTest(TestInfo testInfo) {
        log.info("TEST THREAD = {} | TEST CLASS = {}", Thread.currentThread().getName(), getClass().getName());
        log.info("========== START {} ==========", testInfo.getDisplayName());
    }

    @AfterEach
    void afterEachTest(TestInfo testInfo) {
        log.info("========== END {} ==========", testInfo.getDisplayName());
    }

    @AfterAll
    public static void globalTeardown() {
        long duration = System.currentTimeMillis() - suiteStartTime;

        log.info("================================================");
        log.info("========== TEST SUITE END ==========");
        log.info("Total Execution Time: {} ms", duration);
        log.info("================================================");
    }
}