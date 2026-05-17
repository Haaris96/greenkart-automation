package com.greenkart.tests;

import com.greenkart.base.BaseTest;
import com.greenkart.constants.Constants;
import com.greenkart.dataproviders.TestDataProvider;
import com.greenkart.pages.CartPage;
import com.greenkart.pages.CheckoutPage;
import com.greenkart.pages.HomePage;
import com.greenkart.pages.OrderConfirmationPage;
import com.greenkart.utils.ExtentReportManager;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Map;

/**
 * TestNG tests for the cart panel and checkout page (coupon application).
 */
public class CartTest extends BaseTest {

    @Test(groups = {Constants.GROUP_SMOKE}, description = "Apply valid coupon and verify discount")
    public void applyValidCoupon() {
        HomePage homePage = new HomePage(driver);
        homePage.addProductToCart("Brocolli");
        CartPage cartPage = homePage.proceedToCart();
        Assert.assertTrue(cartPage.isProductInCart("Brocolli"), "Product should be in cart panel");

        CheckoutPage checkoutPage = cartPage.proceedToCheckout();
        checkoutPage.applyCoupon(Constants.VALID_COUPON);
        Assert.assertTrue(checkoutPage.isCouponAppliedSuccessfully(),
                "Valid coupon should be applied. Discount: " + checkoutPage.getCouponMessage());
        String discounted = checkoutPage.getDiscountedPrice();
        Assert.assertFalse(discounted.isEmpty(), "Discounted price should be shown");
        ExtentReportManager.logPass("Valid coupon applied. Discounted price: " + discounted);
    }

    @Test(groups = {Constants.GROUP_REGRESSION}, description = "Apply invalid coupon")
    public void applyInvalidCoupon() {
        HomePage homePage = new HomePage(driver);
        homePage.addProductToCart("Brocolli");
        CartPage cartPage = homePage.proceedToCart();
        CheckoutPage checkoutPage = cartPage.proceedToCheckout();
        checkoutPage.applyCoupon(Constants.INVALID_COUPON);
        Assert.assertFalse(checkoutPage.isCouponAppliedSuccessfully(),
                "Invalid coupon should not apply a discount");
        ExtentReportManager.logPass("Invalid coupon rejected as expected");
    }

    @Test(groups = {Constants.GROUP_REGRESSION}, description = "Data-driven coupon test from Excel",
          dataProvider = "couponData", dataProviderClass = TestDataProvider.class)
    public void couponTestFromExcel(Map<String, String> data) {
        String coupon   = data.get("couponCode");
        String expected = data.get("expectedResult"); // "valid" or "invalid"

        HomePage homePage = new HomePage(driver);
        homePage.addProductToCart("Brocolli");
        CartPage cartPage = homePage.proceedToCart();
        CheckoutPage checkoutPage = cartPage.proceedToCheckout();
        checkoutPage.applyCoupon(coupon);

        if ("valid".equalsIgnoreCase(expected)) {
            Assert.assertTrue(checkoutPage.isCouponAppliedSuccessfully(),
                    "Coupon " + coupon + " should be valid");
            ExtentReportManager.logPass("Valid coupon applied: " + coupon);
        } else {
            Assert.assertFalse(checkoutPage.isCouponAppliedSuccessfully(),
                    "Coupon " + coupon + " should be invalid");
            ExtentReportManager.logPass("Invalid coupon rejected: " + coupon);
        }
    }

    @Test(groups = {Constants.GROUP_REGRESSION}, description = "Remove item from cart panel")
    public void removeItemFromCart() {
        HomePage homePage = new HomePage(driver);
        homePage.addProductToCart("Brocolli");
        CartPage cartPage = homePage.proceedToCart();
        Assert.assertTrue(cartPage.isProductInCart("Brocolli"), "Product should be in cart");
        cartPage.removeItem(0);
        try { Thread.sleep(700); } catch (InterruptedException ignored) {}
        int count = cartPage.getCartItemCount();
        Assert.assertEquals(count, 0, "Cart panel should be empty after removing item");
        ExtentReportManager.logPass("Item removed from cart successfully");
    }

    @Test(groups = {Constants.GROUP_REGRESSION}, description = "Verify cart panel contains added products",
          dataProvider = "productData", dataProviderClass = TestDataProvider.class)
    public void verifyCartContainsProduct(Map<String, String> data) {
        String productName = data.get("productName");
        HomePage homePage = new HomePage(driver);
        homePage.addProductToCart(productName);
        CartPage cartPage = homePage.proceedToCart();
        Assert.assertTrue(cartPage.isProductInCart(productName),
                "Cart panel should contain: " + productName);
        ExtentReportManager.logPass("Product verified in cart: " + productName);
    }

    @Test(groups = {Constants.GROUP_REGRESSION}, description = "Complete order after coupon")
    public void completeOrderAfterCoupon() {
        HomePage homePage = new HomePage(driver);
        homePage.addProductToCart("Brocolli");
        CartPage cartPage = homePage.proceedToCart();
        CheckoutPage checkoutPage = cartPage.proceedToCheckout();
        checkoutPage.applyCoupon(Constants.VALID_COUPON);
        Assert.assertTrue(checkoutPage.isCouponAppliedSuccessfully(), "Coupon should apply");
        OrderConfirmationPage confirm = checkoutPage.placeOrder();
        Assert.assertTrue(confirm.isOrderSuccessful(), "Order should be confirmed");
        ExtentReportManager.logPass("Order completed after coupon: " + confirm.getOrderId());
    }
}
