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
 * Page Object for the GreenKart home / product listing page.
 * Inherits all common WebDriver actions from BasePage (polymorphism in action).
 */
public class HomePage extends BasePage {

    private static final Logger log = LogManager.getLogger(HomePage.class);

    // ─── Locators ──────────────────────────────────────────────────────────────

    @FindBy(css = "input.search-keyword")
    private WebElement searchBox;

    @FindBy(css = "button.search-button")
    private WebElement searchButton;

    @FindBy(css = "div.products")
    private WebElement productsContainer;

    @FindBy(css = ".product-name")
    private List<WebElement> productNames;

    @FindBy(css = "h4.product-name")
    private List<WebElement> allProductNames;

    @FindBy(css = ".product-price")
    private List<WebElement> productPrices;

    @FindBy(xpath = "//button[normalize-space(text())='ADD TO CART']")
    private List<WebElement> addToCartButtons;

    @FindBy(css = "p.cart-count")
    private WebElement cartCount;

    @FindBy(css = "a.cart-icon")
    private WebElement cartIcon;

    @FindBy(css = ".brand")
    private WebElement brandLogo;

    @FindBy(css = ".offers")
    private WebElement offersLink;

    @FindBy(css = "select.search-limit")
    private WebElement itemsPerPage;

    // ─── Constructor ──────────────────────────────────────────────────────────

    public HomePage(WebDriver driver) {
        super(driver);
    }

    // ─── Page Actions ─────────────────────────────────────────────────────────

    public HomePage navigateTo(String url) {
        driver.get(url);
        waitForPageLoad();
        log.info("Navigated to: {}", url);
        ExtentReportManager.logInfo("Navigated to GreenKart: " + url);
        return this;
    }

    public HomePage searchProduct(String productName) {
        waitForVisibility(searchBox);
        clearAndType(searchBox, productName);
        click(searchButton);
        // Wait for Angular to re-render the filtered product list
        hardWait(1000);
        log.info("Searched for product: {}", productName);
        ExtentReportManager.logInfo("Searched for product: " + productName);
        return this;
    }

    /**
     * Adds the specified product to cart by matching name within each product container.
     * Uses a container-based approach for reliable index-independent mapping.
     * Product name format on site: "Brocolli - 1 Kg" — we match by startsWith.
     */
    public HomePage addProductToCart(String productName) {
        // Wait until products have non-empty text (AngularJS data binding)
        wait.until(d -> {
            List<WebElement> els = d.findElements(By.cssSelector("h4.product-name"));
            return !els.isEmpty() && els.stream().anyMatch(e -> {
                try { return !e.getText().trim().isEmpty(); } catch (Exception ex) { return false; }
            });
        });

        // Use each product container to find matching name → button pair
        List<WebElement> containers = driver.findElements(By.cssSelector("div.product"));
        for (WebElement container : containers) {
            List<WebElement> nameEls = container.findElements(By.cssSelector("h4.product-name"));
            if (nameEls.isEmpty()) continue;
            String nameText = nameEls.get(0).getText().trim();
            if (nameText.toLowerCase().startsWith(productName.toLowerCase())) {
                // Find the ADD TO CART button within this product container
                List<WebElement> btns = container.findElements(
                        By.xpath(".//button[normalize-space(text())='ADD TO CART']"));
                if (!btns.isEmpty()) {
                    scrollToElement(btns.get(0));
                    jsClick(btns.get(0));
                    log.info("Added '{}' to cart (full text: '{}')", productName, nameText);
                    ExtentReportManager.logInfo("Added product to cart: " + productName);
                    return this;
                }
            }
        }
        log.warn("Product '{}' not found in {} containers", productName, containers.size());
        throw new RuntimeException("Product not found: " + productName);
    }

    /**
     * Adds a product to cart N times (quantity).
     */
    public HomePage addProductToCart(String productName, int quantity) {
        for (int i = 0; i < quantity; i++) {
            addProductToCart(productName);
        }
        return this;
    }

    /**
     * Adds ALL displayed products to cart.
     */
    public HomePage addAllProductsToCart() {
        wait.until(d -> !d.findElements(By.xpath("//button[normalize-space(text())='ADD TO CART']")).isEmpty());
        List<WebElement> buttons = driver.findElements(
                By.xpath("//button[normalize-space(text())='ADD TO CART']"));
        buttons.forEach(this::jsClick);
        log.info("Added all {} products to cart", buttons.size());
        ExtentReportManager.logInfo("Added all products to cart: " + buttons.size());
        return this;
    }

    public int getCartCount() {
        try {
            // Cart count: <a class="cart-icon"><img/><span class="cart-count">N</span></a>
            // Use innerHTML because getText() may return '' for Angular-bound spans
            List<WebElement> cartIcons = driver.findElements(By.cssSelector("a.cart-icon"));
            if (!cartIcons.isEmpty()) {
                String inner = cartIcons.get(0).getAttribute("innerHTML");
                java.util.regex.Matcher m = java.util.regex.Pattern
                        .compile("cart-count[^>]*>(\\d+)<").matcher(inner);
                if (m.find()) return Integer.parseInt(m.group(1));
                // Also try getText on the span
                List<WebElement> spans = cartIcons.get(0).findElements(By.cssSelector("span.cart-count"));
                if (!spans.isEmpty()) {
                    String txt = spans.get(0).getText().trim();
                    if (!txt.isEmpty()) return Integer.parseInt(txt);
                }
            }
            return 0;
        } catch (Exception e) {
            log.warn("Could not read cart count: {}", e.getMessage());
            return 0;
        }
    }

    public CartPage proceedToCart() {
        // Cart icon: <a class="cart-icon" href="#">
        List<WebElement> cartIcons = driver.findElements(By.cssSelector("a.cart-icon"));
        if (!cartIcons.isEmpty()) {
            jsClick(cartIcons.get(0));
            log.info("Clicked a.cart-icon");
        } else {
            // Fallback: parent of cart image
            List<WebElement> imgs = driver.findElements(By.cssSelector("img[alt='Cart']"));
            if (!imgs.isEmpty()) {
                WebElement parent = (WebElement)
                    ((JavascriptExecutor) driver).executeScript("return arguments[0].parentElement;", imgs.get(0));
                jsClick(parent);
                log.info("Clicked cart icon via JS parent");
            }
        }
        ExtentReportManager.logInfo("Proceeding to cart");
        hardWait(1500); // wait for cart panel to slide in
        return new CartPage(driver);
    }

    public List<String> getDisplayedProductNames() {
        // Wait for text to be populated
        wait.until(d -> {
            List<WebElement> els = d.findElements(By.cssSelector("h4.product-name"));
            return !els.isEmpty() && els.stream().anyMatch(e -> {
                try { return !e.getText().trim().isEmpty(); } catch (Exception ex) { return false; }
            });
        });
        List<WebElement> elements = driver.findElements(By.cssSelector("h4.product-name"));
        // Return the base name part before " - " (e.g., "Brocolli - 1 Kg" → "Brocolli")
        return elements.stream().map(e -> {
            try {
                String text = e.getText().trim();
                int dashIdx = text.indexOf(" - ");
                return dashIdx > 0 ? text.substring(0, dashIdx) : text;
            } catch (Exception ex) { return ""; }
        }).toList();
    }

    public List<String> getDisplayedProductPrices() {
        List<WebElement> elements = driver.findElements(By.cssSelector(".product-price"));
        return elements.stream().map(e -> { try { return e.getText().trim(); } catch (Exception ex) { return ""; }}).toList();
    }

    public int getDisplayedProductCount() {
        try {
            // Short wait for any product to appear
            wait.until(d -> !d.findElements(By.cssSelector("h4.product-name")).isEmpty());
        } catch (Exception e) {
            // No products returned – possibly empty search result
        }
        return driver.findElements(By.cssSelector("h4.product-name")).size();
    }

    public boolean isSearchResultEmpty() {
        // Give Angular time to update the DOM before declaring it empty
        hardWait(1500);
        return driver.findElements(By.cssSelector("h4.product-name")).isEmpty();
    }

    public void selectItemsPerPage(String value) {
        // The items-per-page selector varies across site versions – try common selectors
        String[] selectors = {"select.search-limit", "select[name='search-limit']", "select.limit"};
        for (String sel : selectors) {
            List<WebElement> found = driver.findElements(By.cssSelector(sel));
            if (!found.isEmpty()) {
                selectDropdownByVisibleText(found.get(0), value);
                ExtentReportManager.logInfo("Selected items per page: " + value);
                return;
            }
        }
        log.warn("Items-per-page dropdown not found; skipping selection");
    }

    @Override
    public boolean isPageLoaded() {
        try {
            return isDisplayed(productsContainer) && isDisplayed(searchBox);
        } catch (Exception e) {
            return false;
        }
    }
}
