package com.greenkart.pages;

import com.greenkart.base.BasePage;
import com.greenkart.utils.ExtentReportManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import java.util.List;

/**
 * Page Object for the GreenKart checkout summary page (#/cart).
 * This page appears after clicking PROCEED TO CHECKOUT from the cart panel.
 * It has: order table, promo code input, total amount, and Place Order button.
 */
public class CheckoutPage extends BasePage {

    private static final Logger log = LogManager.getLogger(CheckoutPage.class);

    // ─── Locators ──────────────────────────────────────────────────────────────

    @FindBy(css = "table.cartTable")
    private WebElement orderTable;

    @FindBy(css = "input.promoCode")
    private WebElement couponInput;

    @FindBy(css = "button.promoBtn")
    private WebElement applyCouponButton;

    @FindBy(css = "span.totAmt")
    private WebElement totalAmount;

    @FindBy(css = "span.discountPerc")
    private WebElement discountPercent;

    @FindBy(css = "span.discountAmt")
    private WebElement discountedAmount;

    @FindBy(css = "table.cartTable p.product-name")
    private List<WebElement> summaryProductNames;

    // ─── Constructor ──────────────────────────────────────────────────────────

    public CheckoutPage(WebDriver driver) {
        super(driver);
    }

    // ─── Page Actions ─────────────────────────────────────────────────────────

    public CheckoutPage applyCoupon(String couponCode) {
        waitForVisibility(couponInput);
        // Use JS to set value + dispatch Angular-compatible input/change events
        ((JavascriptExecutor) driver).executeScript(
            "arguments[0].value = arguments[1];" +
            "arguments[0].dispatchEvent(new Event('input', {bubbles:true}));" +
            "arguments[0].dispatchEvent(new Event('change', {bubbles:true}));",
            couponInput, couponCode);
        click(applyCouponButton);
        // Wait up to 5s for the discount % to change from "0%"
        try {
            wait.until(d -> {
                try {
                    String txt = discountPercent.getText().trim();
                    return !txt.isEmpty() && !txt.equals("0%");
                } catch (Exception ex) {
                    return false;
                }
            });
        } catch (Exception ignored) {
            hardWait(2500); // fallback wait
        }
        log.info("Applied coupon: {}", couponCode);
        ExtentReportManager.logInfo("Applied coupon: " + couponCode);
        return this;
    }

    public boolean isCouponAppliedSuccessfully() {
        try {
            String discount = discountPercent.getText().trim();
            return !discount.isEmpty() && !discount.equals("0%");
        } catch (Exception e) {
            return false;
        }
    }

    public String getCouponMessage() {
        try {
            return discountPercent.getText().trim();
        } catch (Exception e) {
            return "";
        }
    }

    public String getTotalAmount() {
        try {
            return totalAmount.getText().trim();
        } catch (Exception e) {
            return "";
        }
    }

    public String getDiscountedPrice() {
        try {
            return discountedAmount.getText().trim();
        } catch (Exception e) {
            return "";
        }
    }

    public List<String> getSummaryProductNames() {
        return summaryProductNames.stream().map(e -> {
            try {
                String txt = e.getText().trim();
                int dash = txt.indexOf(" - ");
                return dash > 0 ? txt.substring(0, dash) : txt;
            } catch (Exception ex) { return ""; }
        }).toList();
    }

    public boolean isSummaryDisplayed() {
        try {
            return isDisplayed(orderTable);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Full order flow:
     * 1. Click Place Order (on #/cart page)
     * 2. Navigate to #/country page: select India, check T&C checkbox, click Proceed
     * 3. Return OrderConfirmationPage (success message shown on #/country page)
     */
    public OrderConfirmationPage placeOrder() {
        // Step 1: Click Place Order
        WebElement placeOrderBtn = wait.until(d -> {
            List<WebElement> btns = d.findElements(By.xpath("//button[normalize-space(text())='Place Order']"));
            return btns.isEmpty() ? null : btns.get(0);
        });
        if (placeOrderBtn != null) {
            jsClick(placeOrderBtn);
            log.info("Clicked Place Order → navigating to country page");
            ExtentReportManager.logInfo("Clicked Place Order");
        }

        // Step 2: Handle country selection page (#/country)
        hardWait(1500); // wait for page transition
        // Select a country (India by default)
        List<WebElement> countrySelects = driver.findElements(By.tagName("select"));
        if (!countrySelects.isEmpty()) {
            try {
                new org.openqa.selenium.support.ui.Select(countrySelects.get(0)).selectByValue("India");
                log.info("Selected country: India");
            } catch (Exception e) {
                log.warn("Could not select India, trying first option: {}", e.getMessage());
                new org.openqa.selenium.support.ui.Select(countrySelects.get(0)).selectByIndex(1);
            }
            hardWait(300);
        }

        // Check T&C checkbox
        List<WebElement> checkboxes = driver.findElements(By.cssSelector("input.chkAgree, input[type='checkbox']"));
        if (!checkboxes.isEmpty() && !checkboxes.get(0).isSelected()) {
            jsClick(checkboxes.get(0));
            log.info("Checked T&C checkbox");
            hardWait(300);
        }

        // Step 3: Click Proceed
        List<WebElement> proceedBtns = driver.findElements(By.xpath("//button[normalize-space(text())='Proceed']"));
        if (!proceedBtns.isEmpty()) {
            jsClick(proceedBtns.get(0));
            log.info("Clicked Proceed on country page");
            ExtentReportManager.logInfo("Clicked Proceed → order placed");
        }

        hardWait(2000); // wait for confirmation text to appear
        return new OrderConfirmationPage(driver);
    }

    /**
     * Kept for backward compatibility with test steps that call acceptTermsAndProceed().
     * On this site, T&C is handled in the order placement flow itself.
     */
    public CheckoutPage acceptTermsAndProceed() {
        log.info("acceptTermsAndProceed() called — T&C handled in placeOrder() for this site");
        return this;
    }

    public OrderConfirmationPage acceptTermsAndPlaceOrder() {
        return placeOrder();
    }

    @Override
    public boolean isPageLoaded() {
        try {
            return !driver.findElements(By.cssSelector("table.cartTable")).isEmpty()
                    || !driver.findElements(By.cssSelector("input.promoCode")).isEmpty();
        } catch (Exception e) {
            return false;
        }
    }
}
