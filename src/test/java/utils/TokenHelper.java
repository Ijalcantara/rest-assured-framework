package utils;

import core.TestDataManager;

public class TokenHelper {

    private TokenHelper() {}

    public static String getValidUserToken() {
        return TestDataManager.getToken("validUser");
    }
}