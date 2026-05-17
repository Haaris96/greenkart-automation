package com.greenkart.constants;

/**
 * Application-wide constants. No magic strings scattered across the code.
 */
public final class Constants {

    private Constants() {}

    // URLs
    public static final String BASE_URL = "https://rahulshettyacademy.com/seleniumPractise/";

    // Timeouts (seconds)
    public static final int IMPLICIT_WAIT   = 10;
    public static final int EXPLICIT_WAIT   = 15;
    public static final int FLUENT_WAIT     = 20;
    public static final int FLUENT_POLLING  = 500; // ms
    public static final int PAGE_LOAD_WAIT  = 30;

    // File paths
    public static final String CONFIG_FILE     = "src/test/resources/config/config.properties";
    public static final String TEST_DATA_FILE  = "src/test/resources/testdata/testdata.xlsx";
    public static final String EXTENT_CONFIG   = "src/test/resources/config/extent.properties";
    public static final String LOG4J_CONFIG    = "src/test/resources/config/log4j2.xml";
    public static final String REPORTS_DIR     = "test-output/reports/";
    public static final String SCREENSHOTS_DIR = "test-output/screenshots/";

    // Excel sheet names
    public static final String SHEET_PRODUCTS  = "Products";
    public static final String SHEET_USERS     = "Users";
    public static final String SHEET_COUPONS   = "Coupons";
    public static final String SHEET_ORDERS    = "Orders";

    // Coupon codes
    public static final String VALID_COUPON   = "rahulshettyacademy";
    public static final String INVALID_COUPON = "INVALID123";

    // Browser options
    public static final String CHROME  = "chrome";
    public static final String FIREFOX = "firefox";
    public static final String EDGE    = "edge";

    // Environments
    public static final String ENV_STAGING = "staging";
    public static final String ENV_PROD    = "prod";

    // Test groups
    public static final String GROUP_SMOKE      = "smoke";
    public static final String GROUP_REGRESSION = "regression";
    public static final String GROUP_E2E        = "e2e";
}
