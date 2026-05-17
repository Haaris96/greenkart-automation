package com.greenkart.utils;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.MediaEntityBuilder;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;
import com.greenkart.constants.Constants;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Thread-safe ExtentReports manager.
 * Produces a single HTML Spark report with:
 *   - Pass/Fail/Skip filter buttons
 *   - Inline screenshots
 *   - Step-level logs
 */
public class ExtentReportManager {

    private static final Logger log = LogManager.getLogger(ExtentReportManager.class);
    private static ExtentReports extent;
    private static final ThreadLocal<ExtentTest> testThreadLocal = new ThreadLocal<>();

    private ExtentReportManager() {}

    public static synchronized ExtentReports getInstance() {
        if (extent == null) {
            String timestamp  = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String reportPath = Constants.REPORTS_DIR + "ExtentReport_" + timestamp + ".html";

            new java.io.File(Constants.REPORTS_DIR).mkdirs();

            ExtentSparkReporter spark = new ExtentSparkReporter(reportPath);
            spark.config().setReportName("GreenKart Automation Report");
            spark.config().setDocumentTitle("GreenKart Test Results");
            spark.config().setTheme(Theme.DARK);
            spark.config().setEncoding("UTF-8");
            // Enable filter buttons (pass/fail/skip) — built-in to Spark reporter
            spark.config().setTimelineEnabled(true);

            extent = new ExtentReports();
            extent.attachReporter(spark);
            extent.setSystemInfo("Application", "GreenKart");
            extent.setSystemInfo("Environment", System.getProperty("env", "staging"));
            extent.setSystemInfo("Browser",     System.getProperty("browser", "chrome"));
            extent.setSystemInfo("OS",          System.getProperty("os.name"));
            extent.setSystemInfo("Java",        System.getProperty("java.version"));

            log.info("ExtentReports initialised: {}", reportPath);
        }
        return extent;
    }

    public static ExtentTest createTest(String testName) {
        ExtentTest test = getInstance().createTest(testName);
        testThreadLocal.set(test);
        return test;
    }

    public static ExtentTest createTest(String testName, String description) {
        ExtentTest test = getInstance().createTest(testName, description);
        testThreadLocal.set(test);
        return test;
    }

    public static ExtentTest getTest() {
        return testThreadLocal.get();
    }

    public static void removeTest() {
        testThreadLocal.remove();
    }

    // ─── Logging helpers ─────────────────────────────────────────────────────

    public static void logInfo(String message) {
        if (getTest() != null) getTest().log(Status.INFO, message);
        log.info(message);
    }

    public static void logPass(String message) {
        if (getTest() != null) getTest().log(Status.PASS, message);
        log.info("[PASS] {}", message);
    }

    public static void logFail(String message) {
        if (getTest() != null) getTest().log(Status.FAIL, message);
        log.error("[FAIL] {}", message);
    }

    public static void logSkip(String message) {
        if (getTest() != null) getTest().log(Status.SKIP, message);
        log.warn("[SKIP] {}", message);
    }

    public static void logWarning(String message) {
        if (getTest() != null) getTest().log(Status.WARNING, message);
        log.warn("[WARN] {}", message);
    }

    public static void attachScreenshot(String testName) {
        try {
            String base64 = ScreenshotUtils.captureScreenshotAsBase64();
            if (!base64.isEmpty() && getTest() != null) {
                getTest().fail("Screenshot on failure",
                        MediaEntityBuilder.createScreenCaptureFromBase64String(base64, testName).build());
            }
        } catch (Exception e) {
            log.warn("Could not attach screenshot to extent report: {}", e.getMessage());
        }
    }

    public static void attachScreenshotToStep(String stepName) {
        try {
            String base64 = ScreenshotUtils.captureScreenshotAsBase64();
            if (!base64.isEmpty() && getTest() != null) {
                getTest().info(stepName,
                        MediaEntityBuilder.createScreenCaptureFromBase64String(base64, stepName).build());
            }
        } catch (Exception e) {
            log.warn("Could not attach step screenshot: {}", e.getMessage());
        }
    }

    public static synchronized void flush() {
        if (extent != null) {
            extent.flush();
            log.info("ExtentReports flushed");
        }
    }
}
