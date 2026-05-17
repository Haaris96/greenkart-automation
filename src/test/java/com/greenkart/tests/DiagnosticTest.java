package com.greenkart.tests;

import com.greenkart.base.BaseTest;
import com.greenkart.base.DriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.annotations.Test;

import java.util.List;

/**
 * Temporary diagnostic test to discover actual CSS selectors on the GreenKart site.
 * Not part of the permanent suite — delete after debugging.
 */
public class DiagnosticTest extends BaseTest {

    @Test
    public void discoverPageSelectors() throws InterruptedException {
        Thread.sleep(3000); // let Angular finish rendering

        System.out.println("=== PAGE TITLE: " + driver.getTitle());
        System.out.println("=== URL: " + driver.getCurrentUrl());

        // Check all h4 elements
        List<WebElement> h4s = driver.findElements(By.tagName("h4"));
        System.out.println("=== h4 count: " + h4s.size());
        for (WebElement h4 : h4s) {
            System.out.println("  h4 class='" + h4.getAttribute("class") + "' text='" + h4.getText() + "'");
        }

        // Check h4.product-name specifically
        List<WebElement> names = driver.findElements(By.cssSelector("h4.product-name"));
        System.out.println("=== h4.product-name count: " + names.size());
        for (int i = 0; i < Math.min(names.size(), 10); i++) {
            System.out.println("  [" + i + "] text='" + names.get(i).getText() + "'");
        }

        // Check p.product-name
        List<WebElement> pNames = driver.findElements(By.cssSelector("p.product-name"));
        System.out.println("=== p.product-name count: " + pNames.size());
        for (WebElement p : pNames) {
            System.out.println("  p text='" + p.getText() + "'");
        }

        // Check .product-name (any element)
        List<WebElement> anyNames = driver.findElements(By.cssSelector(".product-name"));
        System.out.println("=== .product-name (any tag) count: " + anyNames.size());
        for (int i = 0; i < Math.min(anyNames.size(), 10); i++) {
            WebElement el = anyNames.get(i);
            System.out.println("  [" + i + "] tag='" + el.getTagName()
                    + "' class='" + el.getAttribute("class")
                    + "' text='" + el.getText().replace("\n", "\\n") + "'");
        }

        // Check buttons
        List<WebElement> buttons = driver.findElements(By.cssSelector("button"));
        System.out.println("=== button count: " + buttons.size());
        for (int i = 0; i < Math.min(buttons.size(), 5); i++) {
            WebElement btn = buttons.get(i);
            System.out.println("  btn[" + i + "] class='" + btn.getAttribute("class") + "' text='" + btn.getText() + "'");
        }

        // Cart icon
        List<WebElement> cartIcons = driver.findElements(By.cssSelector("a img[alt='Cart']"));
        System.out.println("=== cart icon (a img[alt='Cart']): " + cartIcons.size());

        // Cart count
        List<WebElement> cartCount = driver.findElements(By.cssSelector("p.cart-count"));
        System.out.println("=== p.cart-count: " + cartCount.size());

        // Search input
        List<WebElement> searchInputs = driver.findElements(By.cssSelector("input.search-keyword"));
        System.out.println("=== input.search-keyword: " + searchInputs.size());

        // Print first product div HTML snippet
        List<WebElement> products = driver.findElements(By.cssSelector("div.product"));
        System.out.println("=== div.product count: " + products.size());

        // Add to cart and check what happens
        System.out.println("=== Adding Brocolli to cart via container approach ===");
        List<WebElement> containers2 = driver.findElements(By.cssSelector("div.product"));
        for (WebElement c : containers2) {
            List<WebElement> ns = c.findElements(By.cssSelector("h4.product-name"));
            if (ns.isEmpty()) continue;
            String txt = ns.get(0).getText().trim();
            if (txt.toLowerCase().startsWith("brocolli")) {
                List<WebElement> bs = c.findElements(By.xpath(".//button[normalize-space(text())='ADD TO CART']"));
                System.out.println("  Found container with '" + txt + "', buttons: " + bs.size());
                if (!bs.isEmpty()) {
                    bs.get(0).click();
                    Thread.sleep(1000);
                    System.out.println("  Clicked ADD TO CART");
                }
                break;
            }
        }

        // Now check cart-related elements
        Thread.sleep(2000);
        System.out.println("=== Cart elements after add ===");
        String[] cartSels = {"p.cart-count","span.cart-count",".cart-count","[class*='cart']",
                             ".header-cart","img[alt='Cart']","a img[alt='Cart']"};
        for (String cs : cartSels) {
            List<WebElement> ces = driver.findElements(By.cssSelector(cs));
            System.out.println("  " + cs + ": " + ces.size() +
                    (ces.isEmpty() ? "" : " text='" + ces.get(0).getText() + "'"));
        }

        // Check cart icon parent
        List<WebElement> cartImg = driver.findElements(By.cssSelector("img[alt='Cart']"));
        if (!cartImg.isEmpty()) {
            System.out.println("  Cart img parent tag: " +
                ((org.openqa.selenium.JavascriptExecutor)driver)
                    .executeScript("return arguments[0].parentElement.tagName", cartImg.get(0)));
            System.out.println("  Cart img parent class: " +
                ((org.openqa.selenium.JavascriptExecutor)driver)
                    .executeScript("return arguments[0].parentElement.className", cartImg.get(0)));
            System.out.println("  Cart img parent outerHTML: " +
                ((org.openqa.selenium.JavascriptExecutor)driver)
                    .executeScript("return arguments[0].parentElement.outerHTML", cartImg.get(0)));
        }

        // Click cart icon and examine cart panel
        Thread.sleep(500);
        List<WebElement> cartIcon2 = driver.findElements(By.cssSelector("a.cart-icon"));
        if (!cartIcon2.isEmpty()) {
            cartIcon2.get(0).click();
            Thread.sleep(2000);
            System.out.println("=== Clicked cart icon ===");

            // Check cart panel elements
            System.out.println("Cart panel: " + driver.findElements(By.cssSelector(".cart-preview")).size());
            System.out.println("Cart panel active: " + driver.findElements(By.cssSelector(".cart-preview.active")).size());

            // Find buttons in cart
            List<WebElement> cartBtns = driver.findElements(By.cssSelector(".cart-preview button, .cart button"));
            System.out.println("Buttons in cart: " + cartBtns.size());
            for (WebElement b : cartBtns) {
                System.out.println("  btn text='" + b.getText() + "' class='" + b.getAttribute("class") + "'");
            }

            // Check proceed button variations
            String[] proceedSels = {"button.proceed-btn", "input[value='PROCEED']", "button[class*='proceed']",
                "a[class*='proceed']", ".cart-preview a", ".cart-preview button"};
            for (String ps : proceedSels) {
                List<WebElement> found = driver.findElements(By.cssSelector(ps));
                System.out.println("  " + ps + ": " + found.size() +
                        (found.isEmpty() ? "" : " text='" + found.get(0).getText() + "'"));
            }

            // Print cart panel HTML
            List<WebElement> panel = driver.findElements(By.cssSelector(".cart-preview"));
            if (!panel.isEmpty()) {
                String html = (String)((org.openqa.selenium.JavascriptExecutor)driver)
                        .executeScript("return arguments[0].innerHTML.substring(0, 1500)", panel.get(0));
                System.out.println("=== Cart panel innerHTML (first 1500): ===");
                System.out.println(html);
            }
        }

        // Click Place Order and check confirmation page
        // PROCEED TO CHECKOUT
        List<WebElement> checkoutBtns = driver.findElements(By.cssSelector(".action-block button"));
        if (!checkoutBtns.isEmpty()) {
            checkoutBtns.get(0).click();
            Thread.sleep(2500);
            System.out.println("=== On Checkout page ===");
            // Click Place Order
            List<WebElement> placeOrderBtns = driver.findElements(By.xpath("//button[normalize-space(text())='Place Order']"));
            System.out.println("  Place Order button count: " + placeOrderBtns.size());
            if (!placeOrderBtns.isEmpty()) {
                placeOrderBtns.get(0).click();
                Thread.sleep(3000);
                System.out.println("=== After Place Order Page ===");
                System.out.println("  URL: " + driver.getCurrentUrl());
                // Get root div HTML
                List<WebElement> rootDivs = driver.findElements(By.cssSelector("#root, .container, main"));
                for (WebElement rd : rootDivs) {
                    String h = rd.getAttribute("innerHTML");
                    if (h.length() > 100) {
                        System.out.println("  Content (" + rd.getAttribute("class") + "): " + h.substring(0, Math.min(1500, h.length())));
                        break;
                    }
                }
                // Check all inputs, selects, buttons
                for (WebElement btn : driver.findElements(By.tagName("button"))) {
                    System.out.println("  btn: text='" + btn.getText() + "' class='" + btn.getAttribute("class") + "'");
                }

                // Select country and check T&C, then click Proceed
                List<WebElement> countrySelect = driver.findElements(By.tagName("select"));
                if (!countrySelect.isEmpty()) {
                    new org.openqa.selenium.support.ui.Select(countrySelect.get(0)).selectByValue("India");
                    Thread.sleep(500);
                }
                List<WebElement> chkBoxes = driver.findElements(By.cssSelector("input.chkAgree, input[type='checkbox']"));
                if (!chkBoxes.isEmpty() && !chkBoxes.get(0).isSelected()) {
                    chkBoxes.get(0).click();
                }
                List<WebElement> proceedBtn = driver.findElements(By.xpath("//button[normalize-space(text())='Proceed']"));
                if (!proceedBtn.isEmpty()) {
                    proceedBtn.get(0).click();
                    Thread.sleep(3000);
                    System.out.println("=== Order Confirmation Page ===");
                    System.out.println("  URL: " + driver.getCurrentUrl());
                    List<WebElement> confRoots = driver.findElements(By.cssSelector("#root, .container"));
                    for (WebElement cr : confRoots) {
                        String h = cr.getAttribute("innerHTML");
                        if (h != null && h.length() > 100) {
                            System.out.println("  HTML: " + h.substring(0, Math.min(1500, h.length())));
                            break;
                        }
                    }
                }
            }
        }

        // Click PROCEED TO CHECKOUT
        List<WebElement> proceedBtns = driver.findElements(By.cssSelector(".cart-preview button"));
        if (!proceedBtns.isEmpty()) {
            proceedBtns.get(0).click();
            Thread.sleep(3000);
            System.out.println("=== After clicking PROCEED TO CHECKOUT ===");
            System.out.println("  URL: " + driver.getCurrentUrl());
            System.out.println("  Title: " + driver.getTitle());

            // Print all buttons on new page
            List<WebElement> newBtns = driver.findElements(By.tagName("button"));
            System.out.println("  Buttons on new page: " + newBtns.size());
            for (WebElement b : newBtns) {
                System.out.println("    btn text='" + b.getText().trim() + "' class='" + b.getAttribute("class") + "'");
            }

            // Print all inputs
            List<WebElement> inputs = driver.findElements(By.tagName("input"));
            System.out.println("  Inputs on new page: " + inputs.size());
            for (WebElement inp : inputs) {
                System.out.println("    input type='" + inp.getAttribute("type") + "' class='" + inp.getAttribute("class") + "' placeholder='" + inp.getAttribute("placeholder") + "'");
            }

            // Key selectors to check
            String[] newSels = {".promo", "input.promoCode", ".promoCode", "input[placeholder*='coupon']",
                "input[placeholder*='promo']", ".order-summary", ".checkout", ".agreement"};
            for (String s : newSels) {
                List<WebElement> found = driver.findElements(By.cssSelector(s));
                System.out.println("  " + s + ": " + found.size() +
                        (found.isEmpty() ? "" : " text='" + found.get(0).getText().trim() + "'"));
            }

            // Print body HTML first 2000 chars
            String bodyHtml = (String)((org.openqa.selenium.JavascriptExecutor)driver)
                    .executeScript("return document.body.innerHTML.substring(0, 2000)");
            System.out.println("=== New page body HTML (first 2000): ===");
            System.out.println(bodyHtml);
        }

        System.out.println("=== DIAGNOSTIC COMPLETE ===");
    }
}
