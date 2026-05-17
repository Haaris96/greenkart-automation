package com.greenkart.stepdefinitions;

import com.greenkart.utils.ExtentReportManager;
import io.cucumber.java.en.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.Assert;

import java.util.List;

/**
 * Step definitions for home page feature scenarios.
 */
public class HomePageSteps {

    private static final Logger log = LogManager.getLogger(HomePageSteps.class);
    private final ScenarioContext context;

    public HomePageSteps(ScenarioContext context) {
        this.context = context;
    }

    @When("I search for product {string}")
    public void iSearchForProduct(String productName) {
        context.getHomePage().searchProduct(productName);
        ExtentReportManager.logInfo("Searched for: " + productName);
    }

    @Then("at least {int} product should be displayed")
    public void atLeastNProductsShouldBeDisplayed(int minCount) {
        int count = context.getHomePage().getDisplayedProductCount();
        Assert.assertTrue(count >= minCount,
                "Expected at least " + minCount + " products but found " + count);
        ExtentReportManager.logPass("Product count verified: " + count);
    }

    @Then("the product {string} should appear in results")
    public void theProductShouldAppearInResults(String productName) {
        List<String> products = context.getHomePage().getDisplayedProductNames();
        boolean found = products.stream().anyMatch(p -> p.toLowerCase().contains(productName.toLowerCase()));
        Assert.assertTrue(found, "Product '" + productName + "' not found in results: " + products);
        ExtentReportManager.logPass("Product found in results: " + productName);
    }

    @Then("no products should be displayed")
    public void noProductsShouldBeDisplayed() {
        boolean isEmpty = context.getHomePage().isSearchResultEmpty();
        Assert.assertTrue(isEmpty, "Expected no products but some were displayed");
        ExtentReportManager.logPass("No products displayed as expected");
    }

    @When("I select {string} items per page")
    public void iSelectItemsPerPage(String count) {
        context.getHomePage().selectItemsPerPage(count);
        ExtentReportManager.logInfo("Selected items per page: " + count);
    }

    @Then("at most {int} products should be displayed")
    public void atMostNProductsShouldBeDisplayed(int maxCount) {
        int count = context.getHomePage().getDisplayedProductCount();
        Assert.assertTrue(count <= maxCount,
                "Expected at most " + maxCount + " products but found " + count);
        ExtentReportManager.logPass("Items per page verified: " + count + " <= " + maxCount);
    }
}
