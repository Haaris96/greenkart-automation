package com.greenkart.stepdefinitions;

import com.greenkart.base.DriverManager;
import com.greenkart.constants.Constants;
import com.greenkart.pages.CartPage;
import com.greenkart.pages.CheckoutPage;
import com.greenkart.pages.HomePage;
import com.greenkart.pages.OrderConfirmationPage;
import com.greenkart.utils.ConfigReader;
import com.greenkart.utils.ExtentReportManager;
import io.cucumber.java.en.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.Assert;

/**
 * Shared step definitions used across multiple feature files.
 * Actual GreenKart flow:
 *   1. Home → Add to cart
 *   2. Click cart icon → cart panel
 *   3. Click PROCEED TO CHECKOUT → checkout page (#/cart) with coupon + Place Order
 *   4. Order confirmation
 */
public class CommonSteps {

    private static final Logger log = LogManager.getLogger(CommonSteps.class);
    private final ScenarioContext context;

    public CommonSteps(ScenarioContext context) {
        this.context = context;
    }

    @Given("I am on the GreenKart home page")
    public void iAmOnTheGreenKartHomePage() {
        String url = System.getProperty("base.url",
                ConfigReader.get("base.url", Constants.BASE_URL));
        HomePage homePage = new HomePage(DriverManager.getDriver());
        homePage.navigateTo(url);
        context.setHomePage(homePage);
        Assert.assertTrue(homePage.isPageLoaded(), "Home page should be loaded");
        ExtentReportManager.logInfo("Navigated to GreenKart home page");
    }

    @When("I add product {string} to the cart")
    public void iAddProductToCart(String product) {
        context.getHomePage().addProductToCart(product);
        ExtentReportManager.logInfo("Added product to cart: " + product);
    }

    @When("I proceed to the cart")
    public void iProceedToTheCart() {
        CartPage cartPage = context.getHomePage().proceedToCart();
        context.setCartPage(cartPage);
        Assert.assertTrue(cartPage.isCartDisplayed(), "Cart panel should be displayed");
        ExtentReportManager.logInfo("Cart panel opened");
    }

    @When("I proceed to checkout")
    public void iProceedToCheckout() {
        CheckoutPage checkoutPage = context.getCartPage().proceedToCheckout();
        context.setCheckoutPage(checkoutPage);
        Assert.assertTrue(checkoutPage.isPageLoaded(), "Checkout page should load");
        ExtentReportManager.logInfo("Proceeded to checkout page");
    }

    @When("I apply coupon code {string}")
    public void iApplyCouponCode(String couponCode) {
        context.getCheckoutPage().applyCoupon(couponCode);
        ExtentReportManager.logInfo("Applied coupon: " + couponCode);
    }

    @When("I accept terms and place the order")
    public void iAcceptTermsAndPlaceOrder() {
        // On GreenKart #/cart there is no T&C checkbox — directly click Place Order
        OrderConfirmationPage confirmPage = context.getCheckoutPage().placeOrder();
        context.setConfirmationPage(confirmPage);
        ExtentReportManager.logInfo("Placed order");
    }

    @Then("the order should be placed successfully")
    public void theOrderShouldBePlacedSuccessfully() {
        boolean success = context.getConfirmationPage().isOrderSuccessful();
        ExtentReportManager.logInfo("Order success: " + success);
        Assert.assertTrue(success, "Order confirmation should be displayed");
        ExtentReportManager.logPass("Order placed successfully");
    }

    @Then("the order confirmation should display an order ID")
    public void theOrderConfirmationShouldDisplayOrderId() {
        String orderId = context.getConfirmationPage().getOrderId();
        Assert.assertFalse(orderId.isEmpty(), "Order ID should not be empty");
        ExtentReportManager.logPass("Order ID displayed: " + orderId);
    }

    @Then("the cart count should be {int}")
    public void theCartCountShouldBe(int expectedCount) {
        int actual = context.getHomePage().getCartCount();
        Assert.assertEquals(actual, expectedCount,
                "Cart count mismatch: expected " + expectedCount + " but got " + actual);
        ExtentReportManager.logPass("Cart count verified: " + actual);
    }
}
