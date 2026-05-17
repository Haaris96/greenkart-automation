package com.greenkart.stepdefinitions;

import com.greenkart.pages.*;

/**
 * Shared scenario context injected via PicoContainer into all step-def classes.
 * Avoids static state and makes parallel execution safe.
 */
public class ScenarioContext {

    private HomePage homePage;
    private CartPage cartPage;
    private CheckoutPage checkoutPage;
    private OrderConfirmationPage confirmationPage;

    public HomePage getHomePage() { return homePage; }
    public void setHomePage(HomePage h) { this.homePage = h; }

    public CartPage getCartPage() { return cartPage; }
    public void setCartPage(CartPage c) { this.cartPage = c; }

    public CheckoutPage getCheckoutPage() { return checkoutPage; }
    public void setCheckoutPage(CheckoutPage c) { this.checkoutPage = c; }

    public OrderConfirmationPage getConfirmationPage() { return confirmationPage; }
    public void setConfirmationPage(OrderConfirmationPage o) { this.confirmationPage = o; }
}
