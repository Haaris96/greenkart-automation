package com.greenkart.pages;

import com.greenkart.base.BasePage;
import com.greenkart.utils.ExtentReportManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import java.util.List;

/**
 * Page Object for the GreenKart cart PREVIEW PANEL.
 * This panel slides in when the user clicks the cart icon on the home page.
 * It shows: cart items, remove buttons, and the PROCEED TO CHECKOUT button.
 * Coupon / order placement are on the NEXT page (CheckoutPage — #/cart).
 */
public class CartPage extends BasePage {

    private static final Logger log = LogManager.getLogger(CartPage.class);

    // ─── Locators ──────────────────────────────────────────────────────────────

    @FindBy(css = ".cart-preview.active")
    private WebElement cartPanel;

    @FindBy(css = ".cart-preview.active ul.cart-items li.cart-item")
    private List<WebElement> cartItems;

    @FindBy(css = ".cart-preview.active p.product-name")
    private List<WebElement> cartProductNames;

    @FindBy(css = ".cart-preview.active a.product-remove")
    private List<WebElement> removeButtons;

    // PROCEED TO CHECKOUT button inside the cart panel's .action-block
    @FindBy(css = ".action-block button")
    private WebElement proceedToCheckoutButton;

    // ─── Constructor ──────────────────────────────────────────────────────────

    public CartPage(WebDriver driver) {
        super(driver);
    }

    // ─── Page Actions ─────────────────────────────────────────────────────────

    public boolean isCartDisplayed() {
        try {
            return !driver.findElements(By.cssSelector(".cart-preview.active")).isEmpty();
        } catch (Exception e) {
            return false;
        }
    }

    public int getCartItemCount() {
        return driver.findElements(By.cssSelector(".cart-preview.active li.cart-item")).size();
    }

    public List<String> getCartProductNames() {
        List<WebElement> names = driver.findElements(By.cssSelector(".cart-preview.active p.product-name"));
        return names.stream().map(e -> {
            try {
                String txt = e.getText().trim();
                // Name format: "Brocolli - 1 Kg" → return base name
                int dash = txt.indexOf(" - ");
                return dash > 0 ? txt.substring(0, dash) : txt;
            } catch (Exception ex) { return ""; }
        }).toList();
    }

    public boolean isProductInCart(String productName) {
        return getCartProductNames().stream()
                .anyMatch(name -> name.toLowerCase().contains(productName.toLowerCase()));
    }

    public CartPage removeItem(int itemIndex) {
        List<WebElement> btns = driver.findElements(By.cssSelector(".cart-preview.active a.product-remove"));
        if (itemIndex < btns.size()) {
            jsClick(btns.get(itemIndex));
            hardWait(500);
            log.info("Removed cart item at index {}", itemIndex);
            ExtentReportManager.logInfo("Removed item from cart at index: " + itemIndex);
        }
        return this;
    }

    /**
     * Clicks PROCEED TO CHECKOUT → navigates to the #/cart checkout page.
     * Returns a CheckoutPage representing the checkout summary / order page.
     */
    public CheckoutPage proceedToCheckout() {
        // Wait for the PROCEED button in the cart panel
        WebElement btn = wait.until(d -> {
            List<WebElement> bs = d.findElements(By.cssSelector(".action-block button"));
            if (!bs.isEmpty()) return bs.get(0);
            // Try XPath fallback
            bs = d.findElements(By.xpath("//button[normalize-space(text())='PROCEED TO CHECKOUT']"));
            return bs.isEmpty() ? null : bs.get(0);
        });
        if (btn != null) {
            jsClick(btn);
            log.info("Clicked PROCEED TO CHECKOUT");
            ExtentReportManager.logInfo("Clicked PROCEED TO CHECKOUT");
        }
        hardWait(2000); // wait for checkout page to load
        return new CheckoutPage(driver);
    }

    @Override
    public boolean isPageLoaded() {
        return isCartDisplayed();
    }
}
