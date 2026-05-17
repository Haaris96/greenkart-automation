package com.greenkart.runners;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;
import org.testng.annotations.DataProvider;

/**
 * Main Cucumber TestNG runner.
 * Runs all feature files sequentially by default.
 * Tags can be overridden from command line: -Dcucumber.filter.tags="@smoke"
 */
@CucumberOptions(
    features = "src/test/resources/features",
    glue     = {"com.greenkart.stepdefinitions", "com.greenkart.hooks"},
    tags     = "@smoke or @regression or @e2e",
    plugin   = {
        "pretty",
        "html:test-output/cucumber-reports/cucumber-report.html",
        "json:test-output/cucumber-reports/cucumber.json",
        "junit:test-output/cucumber-reports/cucumber.xml",
        "com.aventstack.extentreports.cucumber.adapter.ExtentCucumberAdapter:"
    },
    monochrome  = true,
    dryRun      = false,
    publish     = false
)
public class TestRunner extends AbstractTestNGCucumberTests {

    @Override
    @DataProvider(parallel = false)
    public Object[][] scenarios() {
        return super.scenarios();
    }
}
