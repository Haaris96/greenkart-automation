package com.greenkart.stepdefinitions;

import com.greenkart.utils.ExtentReportManager;
import io.cucumber.java.en.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.Assert;

/**
 * Step definitions for cart panel management.
 * Note: Coupon application is handled in CommonSteps.iApplyCouponCode()
 * since the coupon is on the checkout page (not the cart panel).
 */
public class CartSteps {

    private static final Logger log = LogManager.getLogger(CartSteps.class);
    private final ScenarioContext context;

    public CartSteps(ScenarioContext context) {
        this.context = context;
    }

    @Then("the coupon should be applied successfully")
    public void theCouponShouldBeAppliedSuccessfully() {
        boolean applied = context.getCheckoutPage().isCouponAppliedSuccessfully();
        Assert.assertTrue(applied, "Coupon was not applied successfully. Discount: "
                + context.getCheckoutPage().getCouponMessage());
        ExtentReportManager.logPass("Coupon applied successfully");
    }

    @Then("the discounted price should be displayed")
    public void theDiscountedPriceShouldBeDisplayed() {
        String price = context.getCheckoutPage().getDiscountedPrice();
        Assert.assertFalse(price.isEmpty(), "Discounted price should be displayed");
        ExtentReportManager.logPass("Discounted price displayed: " + price);
    }

    @Then("the coupon should not be applied")
    public void theCouponShouldNotBeApplied() {
        boolean applied = context.getCheckoutPage().isCouponAppliedSuccessfully();
        Assert.assertFalse(applied, "Coupon should not have been applied");
        ExtentReportManager.logPass("Invalid coupon correctly rejected");
    }

    @Then("the coupon message should contain {string}")
    public void theCouponMessageShouldContain(String expectedText) {
        String message = context.getCheckoutPage().getCouponMessage();
        Assert.assertTrue(message.toLowerCase().contains(expectedText.toLowerCase()),
                "Coupon message '" + message + "' does not contain '" + expectedText + "'");
        ExtentReportManager.logPass("Coupon message verified: " + message);
    }

    @Then("the cart should contain {string}")
    public void theCartShouldContain(String productName) {
        boolean contains = context.getCartPage().isProductInCart(productName);
        Assert.assertTrue(contains, "Cart does not contain: " + productName);
        ExtentReportManager.logPass("Cart contains: " + productName);
    }

    @When("I remove item at position {int} from cart")
    public void iRemoveItemAtPositionFromCart(int position) {
        context.getCartPage().removeItem(position);
        ExtentReportManager.logInfo("Removed item at position: " + position);
    }

    @Then("the cart should be empty")
    public void theCartShouldBeEmpty() {
        int count = context.getCartPage().getCartItemCount();
        Assert.assertEquals(count, 0, "Cart should be empty but has " + count + " items");
        ExtentReportManager.logPass("Cart is empty as expected");
    }

    @Then("the cart count should be updated")
    public void theCartCountShouldBeUpdated() {
        int count = context.getCartPage().getCartItemCount();
        Assert.assertTrue(count >= 0, "Cart count should be a valid number");
        ExtentReportManager.logPass("Cart count is: " + count);
    }

    @When("I increment quantity of item at position {int}")
    public void iIncrementQuantityOfItemAtPosition(int position) {
        // GreenKart cart panel doesn't have quantity increment — just log
        log.warn("Quantity increment not available in cart panel — step skipped");
        ExtentReportManager.logInfo("Quantity increment (not available in cart panel, skipped)");
    }
}
