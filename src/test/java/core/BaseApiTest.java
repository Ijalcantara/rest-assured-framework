package core;

import io.restassured.RestAssured;
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public abstract class BaseApiTest {

    private static final Logger log = LoggerFactory.getLogger(BaseApiTest.class);

    @BeforeAll
    public static void globalSetup() {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    @BeforeEach
    void beforeEachTest(TestInfo testInfo) {
        System.out.println(
                "TEST THREAD = " + Thread.currentThread().getName() +
                        " | TEST CLASS = " + getClass().getName()
        );
        System.out.println("========== START " + testInfo.getDisplayName() + " ==========");
    }

    @AfterEach
    void afterEachTest(TestInfo testInfo) {
        System.out.println("========== END " + testInfo.getDisplayName() + " ==========");
    }

    @AfterAll
    public static void globalTearDown() {
        // Reset RestAssured configuration to defaults (good practice)
        RestAssured.reset();

        // Log final messages
        log.info("========== GLOBAL TEST END ==========");
        log.info("All tests finished. Resources cleaned up.");
    }
}