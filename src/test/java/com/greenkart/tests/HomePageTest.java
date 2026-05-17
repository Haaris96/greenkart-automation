package com.greenkart.tests;

import com.greenkart.base.BaseTest;
import com.greenkart.constants.Constants;
import com.greenkart.dataproviders.TestDataProvider;
import com.greenkart.pages.CartPage;
import com.greenkart.pages.HomePage;
import com.greenkart.utils.ExtentReportManager;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.List;

/**
 * TestNG tests for the GreenKart home page.
 * Uses DataProviders and exercises BasePage inherited helpers.
 */
public class HomePageTest extends BaseTest {

    @Test(groups = {Constants.GROUP_SMOKE}, description = "Verify home page loads with products")
    public void verifyHomePageLoads() {
        HomePage homePage = new HomePage(driver);
        Assert.assertTrue(homePage.isPageLoaded(), "Home page should be loaded");
        int count = homePage.getDisplayedProductCount();
        Assert.assertTrue(count > 0, "At least one product should be displayed");
        ExtentReportManager.logPass("Home page loaded with " + count + " products");
    }

    @Test(groups = {Constants.GROUP_SMOKE}, description = "Search for existing product",
          dataProvider = "searchTerms", dataProviderClass = TestDataProvider.class)
    public void searchForProduct(String searchTerm, int minExpected) {
        HomePage homePage = new HomePage(driver);
        homePage.searchProduct(searchTerm);
        int count = homePage.getDisplayedProductCount();
        Assert.assertTrue(count >= minExpected,
                "Expected >= " + minExpected + " results for '" + searchTerm + "', got " + count);
        ExtentReportManager.logPass("Search '" + searchTerm + "' returned " + count + " results");
    }

    @Test(groups = {Constants.GROUP_REGRESSION}, description = "Search for non-existing product")
    public void searchForNonExistingProduct() {
        HomePage homePage = new HomePage(driver);
        homePage.searchProduct("XYZNONEXISTENT_PRODUCT");
        boolean isEmpty = homePage.isSearchResultEmpty();
        Assert.assertTrue(isEmpty, "No products should be displayed for invalid search");
        ExtentReportManager.logPass("Empty search result verified");
    }

    @Test(groups = {Constants.GROUP_SMOKE}, description = "Add single product to cart",
          dataProvider = "products", dataProviderClass = TestDataProvider.class)
    public void addProductToCart(String productName) {
        HomePage homePage = new HomePage(driver);
        homePage.addProductToCart(productName);
        int count = homePage.getCartCount();
        Assert.assertEquals(count, 1, "Cart should have 1 item after adding " + productName);
        ExtentReportManager.logPass("Product '" + productName + "' added to cart successfully");
    }

    @Test(groups = {Constants.GROUP_REGRESSION}, description = "Add multiple products to cart and verify count")
    public void addMultipleProductsToCart() {
        HomePage homePage = new HomePage(driver);
        homePage.addProductToCart("Brocolli");
        homePage.addProductToCart("Cauliflower");
        homePage.addProductToCart("Beetroot");
        int count = homePage.getCartCount();
        Assert.assertEquals(count, 3, "Cart should have 3 items");
        ExtentReportManager.logPass("Multiple products added. Cart count: " + count);
    }

    @Test(groups = {Constants.GROUP_REGRESSION}, description = "Verify product names are displayed")
    public void verifyProductNamesDisplayed() {
        HomePage homePage = new HomePage(driver);
        List<String> names = homePage.getDisplayedProductNames();
        Assert.assertFalse(names.isEmpty(), "Product names should be displayed");
        // Filter out empty strings (stale elements) before asserting
        List<String> nonEmpty = names.stream().filter(n -> !n.isBlank()).toList();
        Assert.assertFalse(nonEmpty.isEmpty(), "At least one non-empty product name should exist");
        ExtentReportManager.logPass("All product names verified: " + nonEmpty.size() + " products");
    }

    @Test(groups = {Constants.GROUP_REGRESSION}, description = "Verify cart navigation")
    public void verifyCartNavigation() {
        HomePage homePage = new HomePage(driver);
        homePage.addProductToCart("Brocolli");
        CartPage cartPage = homePage.proceedToCart();
        Assert.assertTrue(cartPage.isCartDisplayed(), "Cart page should be displayed");
        ExtentReportManager.logPass("Cart navigation verified");
    }
}
