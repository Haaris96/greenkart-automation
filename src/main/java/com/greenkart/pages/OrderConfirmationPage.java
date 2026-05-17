package com.greenkart.pages;

import com.greenkart.base.BasePage;
import com.greenkart.utils.ExtentReportManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;

/**
 * Page Object for the GreenKart order confirmation page (#/country after Proceed).
 * The confirmation is a green span: <span style="color:green;font-size:25px">Thank you, your order has been placed successfully...</span>
 */
public class OrderConfirmationPage extends BasePage {

    private static final Logger log = LogManager.getLogger(OrderConfirmationPage.class);

    private static final By SUCCESS_SPAN = By.cssSelector("span[style*='color:green']");
    private static final By SUCCESS_SPAN_XPATH = By.xpath("//span[contains(text(),'Thank you')]");

    public OrderConfirmationPage(WebDriver driver) {
        super(driver);
    }

    public boolean isOrderSuccessful() {
        try {
            // Try CSS selector first
            List<WebElement> spans = driver.findElements(SUCCESS_SPAN);
            if (!spans.isEmpty()) {
                String text = spans.get(0).getText().trim();
                boolean success = text.toLowerCase().contains("thank you") || text.toLowerCase().contains("placed successfully");
                log.info("Order confirmation found (CSS): {}", text.substring(0, Math.min(60, text.length())));
                ExtentReportManager.logInfo("Order placed: " + success);
                return success;
            }
            // Fallback XPath
            spans = driver.findElements(SUCCESS_SPAN_XPATH);
            if (!spans.isEmpty()) {
                log.info("Order confirmation found (XPath)");
                ExtentReportManager.logInfo("Order placed successfully");
                return true;
            }
            log.warn("No order confirmation span found. URL: {}", driver.getCurrentUrl());
            return false;
        } catch (Exception e) {
            log.error("Error checking order confirmation", e);
            return false;
        }
    }

    public String getSuccessMessage() {
        try {
            List<WebElement> spans = driver.findElements(SUCCESS_SPAN);
            if (!spans.isEmpty()) return spans.get(0).getText().trim();
            spans = driver.findElements(SUCCESS_SPAN_XPATH);
            if (!spans.isEmpty()) return spans.get(0).getText().trim();
            return "";
        } catch (Exception e) {
            return "";
        }
    }

    public String getOrderId() {
        // GreenKart does not show an order ID — return the success message text instead
        return getSuccessMessage();
    }

    @Override
    public boolean isPageLoaded() {
        try {
            return !driver.findElements(SUCCESS_SPAN).isEmpty()
                    || !driver.findElements(SUCCESS_SPAN_XPATH).isEmpty();
        } catch (Exception e) {
            return false;
        }
    }
}
