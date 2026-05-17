package com.greenkart.runners;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;
import org.testng.annotations.DataProvider;

/**
 * Runner for smoke tests only — fast subset for CI gates.
 */
@CucumberOptions(
    features = "src/test/resources/features",
    glue     = {"com.greenkart.stepdefinitions", "com.greenkart.hooks"},
    tags     = "@smoke",
    plugin   = {
        "pretty",
        "html:test-output/cucumber-reports/smoke-report.html",
        "json:test-output/cucumber-reports/smoke.json",
        "com.aventstack.extentreports.cucumber.adapter.ExtentCucumberAdapter:"
    },
    monochrome = true
)
public class SmokeTestRunner extends AbstractTestNGCucumberTests {

    @Override
    @DataProvider(parallel = false)
    public Object[][] scenarios() {
        return super.scenarios();
    }
}
