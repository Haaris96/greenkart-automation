package com.greenkart.base;

import com.greenkart.constants.Constants;
import com.greenkart.utils.ConfigReader;
import com.greenkart.utils.ExtentReportManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.testng.ITestResult;
import org.testng.annotations.*;

/**
 * TestNG base class that all test classes extend.
 * Handles driver setup/teardown and extent report lifecycle.
 */
public class BaseTest {

    private static final Logger log = LogManager.getLogger(BaseTest.class);
    protected WebDriver driver;

    @BeforeSuite(alwaysRun = true)
    public void beforeSuite() {
        log.info("=== Test Suite Starting ===");
        ExtentReportManager.getInstance(); // initialise once
    }

    @BeforeMethod(alwaysRun = true)
    public void setUp(java.lang.reflect.Method method) {
        DriverManager.initDriver();
        driver = DriverManager.getDriver();

        String testName = method.getName();
        ExtentReportManager.createTest(testName);
        log.info("--- Test Started: {} ---", testName);

        String url = System.getProperty("base.url",
                ConfigReader.get("base.url", Constants.BASE_URL));
        driver.get(url);
        log.info("Opened URL: {}", url);
        ExtentReportManager.logInfo("Browser opened: " + url);
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown(ITestResult result) {
        String testName = result.getName();
        if (result.getStatus() == ITestResult.FAILURE) {
            log.error("Test FAILED: {}", testName);
            ExtentReportManager.attachScreenshot(testName);
            ExtentReportManager.logFail("Test Failed: " + result.getThrowable().getMessage());
        } else if (result.getStatus() == ITestResult.SKIP) {
            log.warn("Test SKIPPED: {}", testName);
            ExtentReportManager.logSkip("Test Skipped");
        } else {
            log.info("Test PASSED: {}", testName);
            ExtentReportManager.logPass("Test Passed");
        }

        DriverManager.quitDriver();
        ExtentReportManager.removeTest();
        log.info("--- Test Ended: {} ---", testName);
    }

    @AfterSuite(alwaysRun = true)
    public void afterSuite() {
        ExtentReportManager.flush();
        log.info("=== Test Suite Finished ===");
    }
}
