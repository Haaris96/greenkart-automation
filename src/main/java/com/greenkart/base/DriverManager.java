package com.greenkart.base;

import com.greenkart.constants.Constants;
import com.greenkart.utils.ConfigReader;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;

import java.time.Duration;

/**
 * Manages WebDriver lifecycle using ThreadLocal for thread-safe parallel execution.
 * Encapsulates all driver creation logic (factory pattern).
 */
public class DriverManager {

    private static final Logger log = LogManager.getLogger(DriverManager.class);
    private static final ThreadLocal<WebDriver> driverThreadLocal = new ThreadLocal<>();

    private DriverManager() {}

    public static void initDriver() {
        String browser  = System.getProperty("browser",  ConfigReader.get("browser",  Constants.CHROME));
        String headless = System.getProperty("headless", ConfigReader.get("headless", "false"));
        boolean isHeadless = Boolean.parseBoolean(headless);

        log.info("Initialising {} driver | headless={}", browser, isHeadless);

        WebDriver driver;
        switch (browser.toLowerCase()) {
            case Constants.FIREFOX:
                WebDriverManager.firefoxdriver().setup();
                FirefoxOptions ffOpts = new FirefoxOptions();
                if (isHeadless) ffOpts.addArguments("-headless");
                driver = new FirefoxDriver(ffOpts);
                break;

            case Constants.EDGE:
                WebDriverManager.edgedriver().setup();
                EdgeOptions edgeOpts = new EdgeOptions();
                if (isHeadless) edgeOpts.addArguments("--headless");
                driver = new EdgeDriver(edgeOpts);
                break;

            case Constants.CHROME:
            default:
                WebDriverManager.chromedriver().setup();
                ChromeOptions chromeOpts = new ChromeOptions();
                chromeOpts.addArguments("--start-maximized");
                chromeOpts.addArguments("--disable-notifications");
                chromeOpts.addArguments("--disable-popup-blocking");
                if (isHeadless) {
                    chromeOpts.addArguments("--headless=new");
                    chromeOpts.addArguments("--window-size=1920,1080");
                }
                driver = new ChromeDriver(chromeOpts);
                break;
        }

        // Implicit wait set to 0 – we use explicit/fluent waits exclusively.
        // Mixing implicit + explicit waits causes unpredictable behaviour in Selenium 4.
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(0));
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(Constants.PAGE_LOAD_WAIT));

        driverThreadLocal.set(driver);
        log.info("Driver initialised successfully");
    }

    public static WebDriver getDriver() {
        WebDriver driver = driverThreadLocal.get();
        if (driver == null) {
            throw new IllegalStateException("Driver not initialised. Call DriverManager.initDriver() first.");
        }
        return driver;
    }

    public static void quitDriver() {
        WebDriver driver = driverThreadLocal.get();
        if (driver != null) {
            log.info("Quitting driver");
            driver.quit();
            driverThreadLocal.remove();
        }
    }
}
