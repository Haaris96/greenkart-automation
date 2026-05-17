package com.greenkart.tests;

import com.greenkart.base.BaseTest;
import com.greenkart.constants.Constants;
import com.greenkart.dataproviders.TestDataProvider;
import com.greenkart.pages.*;
import com.greenkart.utils.ExtentReportManager;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Map;

/**
 * End-to-end tests covering the complete GreenKart purchase flow.
 * Flow: Home → Add to Cart → Cart Panel → Checkout (#/cart) → Confirmation
 */
public class E2ETest extends BaseTest {

    @Test(groups = {Constants.GROUP_SMOKE, Constants.GROUP_E2E},
          description = "Complete purchase without coupon")
    public void completePurchaseWithoutCoupon() {
        ExtentReportManager.logInfo("Starting E2E test: purchase without coupon");

        HomePage homePage = new HomePage(driver);
        homePage.addProductToCart("Brocolli");
        ExtentReportManager.logInfo("Added Brocolli to cart");

        CartPage cartPage = homePage.proceedToCart();
        Assert.assertTrue(cartPage.isCartDisplayed(), "Cart panel should be displayed");
        ExtentReportManager.logInfo("Cart panel displayed");

        CheckoutPage checkoutPage = cartPage.proceedToCheckout();
        Assert.assertTrue(checkoutPage.isPageLoaded(), "Checkout page should be loaded");
        ExtentReportManager.logInfo("Checkout page loaded. Total: " + checkoutPage.getTotalAmount());

        OrderConfirmationPage confirmPage = checkoutPage.placeOrder();
        Assert.assertTrue(confirmPage.isOrderSuccessful(), "Order should be placed successfully");
        ExtentReportManager.logPass("E2E purchase completed. Order: " + confirmPage.getOrderId());
    }

    @Test(groups = {Constants.GROUP_REGRESSION, Constants.GROUP_E2E},
          description = "Complete purchase with valid coupon code")
    public void completePurchaseWithCoupon() {
        ExtentReportManager.logInfo("Starting E2E test: purchase with coupon");

        HomePage homePage = new HomePage(driver);
        homePage.addProductToCart("Brocolli");
        homePage.addProductToCart("Cauliflower");

        CartPage cartPage = homePage.proceedToCart();
        CheckoutPage checkoutPage = cartPage.proceedToCheckout();

        String totalBefore = checkoutPage.getTotalAmount();
        checkoutPage.applyCoupon(Constants.VALID_COUPON);
        Assert.assertTrue(checkoutPage.isCouponAppliedSuccessfully(), "Coupon should be applied");
        String discounted = checkoutPage.getDiscountedPrice();
        ExtentReportManager.logInfo("Total before: " + totalBefore + " | After discount: " + discounted);

        OrderConfirmationPage confirmPage = checkoutPage.placeOrder();
        Assert.assertTrue(confirmPage.isOrderSuccessful(), "Order should be placed successfully");
        String orderId = confirmPage.getOrderId();
        Assert.assertFalse(orderId.isEmpty(), "Order ID should be present");
        ExtentReportManager.logPass("E2E purchase with coupon completed. Order ID: " + orderId);
    }

    @Test(groups = {Constants.GROUP_E2E, Constants.GROUP_REGRESSION},
          description = "Data-driven E2E purchase from Excel",
          dataProvider = "orderData", dataProviderClass = TestDataProvider.class)
    public void dataDrivenE2EPurchase(Map<String, String> data) {
        String product    = data.get("productName");
        String coupon     = data.get("couponCode");
        boolean useCoupon = !coupon.isEmpty();

        ExtentReportManager.logInfo("E2E test - Product: " + product + " | Coupon: " + coupon);

        HomePage homePage = new HomePage(driver);
        homePage.addProductToCart(product);

        CartPage cartPage = homePage.proceedToCart();
        Assert.assertTrue(cartPage.isProductInCart(product), "Product should be in cart panel");

        CheckoutPage checkoutPage = cartPage.proceedToCheckout();
        Assert.assertTrue(checkoutPage.isPageLoaded(), "Checkout page should load");

        if (useCoupon) {
            checkoutPage.applyCoupon(coupon);
            ExtentReportManager.logInfo("Coupon applied: " + coupon);
        }

        OrderConfirmationPage confirmPage = checkoutPage.placeOrder();
        Assert.assertTrue(confirmPage.isOrderSuccessful(), "Order should be confirmed");
        ExtentReportManager.logPass("Data-driven E2E test passed for product: " + product);
    }

    @Test(groups = {Constants.GROUP_REGRESSION, Constants.GROUP_E2E},
          description = "Purchase multiple products with coupon")
    public void purchaseMultipleProductsWithCoupon() {
        String[] products = {"Brocolli", "Tomato", "Beetroot"};

        HomePage homePage = new HomePage(driver);
        for (String p : products) {
            homePage.addProductToCart(p);
        }

        ExtentReportManager.logInfo("Added " + products.length + " products to cart");

        CartPage cartPage = homePage.proceedToCart();
        CheckoutPage checkoutPage = cartPage.proceedToCheckout();
        checkoutPage.applyCoupon(Constants.VALID_COUPON);
        Assert.assertTrue(checkoutPage.isCouponAppliedSuccessfully(), "Coupon should be applied");

        OrderConfirmationPage confirmPage = checkoutPage.placeOrder();
        Assert.assertTrue(confirmPage.isOrderSuccessful(), "Order should be confirmed");
        ExtentReportManager.logPass("Multi-product purchase with coupon completed");
    }

    @Test(groups = {Constants.GROUP_SMOKE, Constants.GROUP_E2E},
          description = "Search product then purchase")
    public void searchAndPurchase() {
        HomePage homePage = new HomePage(driver);
        homePage.searchProduct("Broc");
        int count = homePage.getDisplayedProductCount();
        Assert.assertTrue(count >= 1, "Search should return results, got: " + count);
        ExtentReportManager.logInfo("Search returned " + count + " products");

        homePage.addProductToCart("Brocolli");
        CartPage cartPage = homePage.proceedToCart();
        CheckoutPage checkoutPage = cartPage.proceedToCheckout();

        OrderConfirmationPage confirmPage = checkoutPage.placeOrder();
        Assert.assertTrue(confirmPage.isOrderSuccessful(), "Order should succeed");
        ExtentReportManager.logPass("Search and purchase flow completed");
    }
}
