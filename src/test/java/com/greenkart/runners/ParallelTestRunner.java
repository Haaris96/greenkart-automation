package com.greenkart.runners;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;
import org.testng.annotations.DataProvider;

/**
 * Parallel Cucumber runner — each scenario runs in its own thread.
 * ThreadLocal in DriverManager and ScenarioContext ensure thread safety.
 */
@CucumberOptions(
    features = "src/test/resources/features",
    glue     = {"com.greenkart.stepdefinitions", "com.greenkart.hooks"},
    tags     = "@smoke or @regression",
    plugin   = {
        "pretty",
        "html:test-output/cucumber-reports/parallel-report.html",
        "json:test-output/cucumber-reports/parallel.json",
        "com.aventstack.extentreports.cucumber.adapter.ExtentCucumberAdapter:"
    },
    monochrome = true,
    dryRun     = false
)
public class ParallelTestRunner extends AbstractTestNGCucumberTests {

    @Override
    @DataProvider(parallel = true)
    public Object[][] scenarios() {
        return super.scenarios();
    }
}
