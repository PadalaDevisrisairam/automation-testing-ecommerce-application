package com.ecommerce.automation.cucumber.steps;

import com.ecommerce.automation.utils.ConfigReader;
import com.ecommerce.automation.utils.DriverFactory;
import com.ecommerce.automation.utils.ScreenshotUtil;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import org.openqa.selenium.WebDriver;

/**
 * Cucumber lifecycle hooks.
 *
 *  @Before  — creates a WebDriver before each scenario
 *  @After   — attaches a screenshot on failure, then quits the driver
 *
 * The driver is stored in the shared ScenarioContext so all step
 * definition classes can access the same instance.
 */
public class Hooks {

    private final ScenarioContext ctx;

    public Hooks(ScenarioContext ctx) {
        this.ctx = ctx;
    }

    @Before(order = 0)
    public void setUp(Scenario scenario) {
        WebDriver driver = DriverFactory.createDriver();
        ctx.setDriver(driver);
        System.out.println("[Cucumber] Starting scenario: " + scenario.getName());
    }

    @After(order = 0)
    public void tearDown(Scenario scenario) {
        WebDriver driver = ctx.getDriver();
        if (driver != null) {
            if (scenario.isFailed() && ConfigReader.screenshotOnFail()) {
                byte[] screenshot = ScreenshotUtil.captureAsBytes(driver);
                scenario.attach(screenshot, "image/png", "Screenshot on failure");
            }
            driver.quit();
            ctx.setDriver(null);
        }
    }
}
