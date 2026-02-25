package core;

import io.restassured.RestAssured;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInfo;

public abstract class BaseApiTest {

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
}