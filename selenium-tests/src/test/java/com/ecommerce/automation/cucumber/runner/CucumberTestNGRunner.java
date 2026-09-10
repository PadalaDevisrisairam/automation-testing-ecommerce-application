package com.ecommerce.automation.cucumber.runner;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;
import org.testng.annotations.DataProvider;

/**
 * Cucumber TestNG Runner.
 *
 * Runs all feature files under src/test/resources/features/.
 * Reports are generated in target/cucumber-reports/.
 *
 * Run this suite via Maven:
 *   mvn test -P cucumber
 *
 * Or run individual tags:
 *   mvn test -P cucumber -Dcucumber.filter.tags="@smoke"
 */
@CucumberOptions(
    features  = "src/test/resources/features",
    glue      = "com.ecommerce.automation.cucumber.steps",
    tags      = "not @wip",
    plugin    = {
        "pretty",
        "html:target/cucumber-reports/cucumber.html",
        "json:target/cucumber-reports/cucumber.json",
        "junit:target/cucumber-reports/cucumber.xml",
        "io.qameta.allure.cucumber7jvm.AllureCucumber7Jvm"
    },
    monochrome = true,
    publish    = false
)
public class CucumberTestNGRunner extends AbstractTestNGCucumberTests {

    /**
     * Enables parallel scenario execution when running with TestNG.
     * Set parallel=true here and configure thread-count in testng-cucumber.xml.
     */
    @Override
    @DataProvider(parallel = false)
    public Object[][] scenarios() {
        return super.scenarios();
    }
}
