package com.greenkart.utils;

import com.greenkart.base.DriverManager;
import com.greenkart.constants.Constants;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;

/**
 * Captures screenshots and returns absolute file paths for attaching to reports.
 */
public class ScreenshotUtils {

    private static final Logger log = LogManager.getLogger(ScreenshotUtils.class);

    private ScreenshotUtils() {}

    /**
     * Takes a screenshot, saves it to the screenshots directory, and returns the path.
     */
    public static String captureScreenshot(String testName) {
        WebDriver driver = DriverManager.getDriver();
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss_SSS").format(new Date());
        String fileName  = testName.replaceAll("[^a-zA-Z0-9_]", "_") + "_" + timestamp + ".png";
        String filePath  = Constants.SCREENSHOTS_DIR + fileName;

        try {
            File src  = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            File dest = new File(filePath);
            dest.getParentFile().mkdirs();
            FileUtils.copyFile(src, dest);
            log.info("Screenshot saved: {}", filePath);
        } catch (IOException e) {
            log.error("Failed to capture screenshot for test: {}", testName, e);
        }
        return filePath;
    }

    /**
     * Returns screenshot as Base64 string (used by ExtentReports inline embedding).
     */
    public static String captureScreenshotAsBase64() {
        try {
            WebDriver driver = DriverManager.getDriver();
            return ((TakesScreenshot) driver).getScreenshotAs(OutputType.BASE64);
        } catch (Exception e) {
            log.warn("Base64 screenshot failed: {}", e.getMessage());
            return "";
        }
    }

    /**
     * Embeds Base64 screenshot as an HTML image tag for reports.
     */
    public static String getBase64ScreenshotHtml() {
        String base64 = captureScreenshotAsBase64();
        if (base64.isEmpty()) return "";
        return "<img src='data:image/png;base64," + base64 + "' width='800'/>";
    }
}
