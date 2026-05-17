package com.greenkart.hooks;

import com.greenkart.base.DriverManager;
import com.greenkart.constants.Constants;
import com.greenkart.utils.ConfigReader;
import com.greenkart.utils.ExtentReportManager;
import com.greenkart.utils.ScreenshotUtils;
import io.cucumber.java.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;

/**
 * Cucumber hooks: Before/After scenario and step lifecycle.
 * Handles driver init/teardown, extent test creation, and screenshot capture on failure.
 */
public class Hooks {

    private static final Logger log = LogManager.getLogger(Hooks.class);

    @BeforeAll
    public static void globalSetup() {
        log.info("=== Cucumber Suite Starting ===");
        ExtentReportManager.getInstance();
    }

    @Before(order = 1)
    public void initDriver(Scenario scenario) {
        DriverManager.initDriver();
        WebDriver driver = DriverManager.getDriver();

        // Navigate to base URL
        String url = System.getProperty("base.url",
                ConfigReader.get("base.url", Constants.BASE_URL));
        driver.get(url);

        // Create ExtentTest for this scenario
        ExtentReportManager.createTest(scenario.getName(), "Tags: " + scenario.getSourceTagNames());
        ExtentReportManager.logInfo("Scenario started: " + scenario.getName());
        log.info("--- Scenario Starting: {} ---", scenario.getName());
    }

    @AfterStep
    public void afterStep(Scenario scenario) {
        // Attach screenshot after each step for full traceability
        try {
            String base64 = ScreenshotUtils.captureScreenshotAsBase64();
            if (!base64.isEmpty()) {
                scenario.attach(
                    java.util.Base64.getDecoder().decode(base64),
                    "image/png",
                    "Step Screenshot"
                );
                ExtentReportManager.attachScreenshotToStep("Step completed");
            }
        } catch (Exception e) {
            log.warn("Could not capture after-step screenshot: {}", e.getMessage());
        }
    }

    @After(order = 1)
    public void tearDown(Scenario scenario) {
        log.info("--- Scenario Ended: {} | Status: {} ---", scenario.getName(), scenario.getStatus());

        if (scenario.isFailed()) {
            // Capture failure screenshot
            try {
                String base64 = ScreenshotUtils.captureScreenshotAsBase64();
                if (!base64.isEmpty()) {
                    scenario.attach(
                        java.util.Base64.getDecoder().decode(base64),
                        "image/png",
                        "Failure Screenshot"
                    );
                }
                ExtentReportManager.attachScreenshot(scenario.getName());
                ExtentReportManager.logFail("Scenario FAILED: " + scenario.getName());
            } catch (Exception e) {
                log.warn("Could not attach failure screenshot: {}", e.getMessage());
            }
        } else {
            ExtentReportManager.logPass("Scenario PASSED: " + scenario.getName());
        }

        DriverManager.quitDriver();
        ExtentReportManager.removeTest();
    }

    @AfterAll
    public static void globalTearDown() {
        ExtentReportManager.flush();
        log.info("=== Cucumber Suite Finished ===");
    }
}
