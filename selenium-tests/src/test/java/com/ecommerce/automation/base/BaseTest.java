package com.ecommerce.automation.base;

import com.ecommerce.automation.utils.ConfigReader;
import com.ecommerce.automation.utils.DriverFactory;
import com.ecommerce.automation.utils.ScreenshotUtil;
import org.openqa.selenium.WebDriver;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

/**
 * BaseTest (TestNG) — inherited by all TestNG test classes.
 *
 * Lifecycle:
 *   @BeforeMethod  → creates a fresh WebDriver
 *   @AfterMethod   → takes screenshot on failure, then quits driver
 *
 * Thread-local storage makes parallel test execution safe.
 */
public class BaseTest {

    private static final ThreadLocal<WebDriver> driverHolder = new ThreadLocal<>();

    protected static final String BASE_URL = ConfigReader.getBaseUrl();

    // ----- Driver management ------------------------------------------------

    @BeforeMethod(alwaysRun = true)
    public void setUp() {
        WebDriver driver = DriverFactory.createDriver();
        driverHolder.set(driver);
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown(ITestResult result) {
        WebDriver driver = getDriver();
        if (driver != null) {
            if (result.getStatus() == ITestResult.FAILURE && ConfigReader.screenshotOnFail()) {
                ScreenshotUtil.capture(driver, result.getName());
            }
            driver.quit();
            driverHolder.remove();
        }
    }

    /** Returns the WebDriver for the current thread. */
    public static WebDriver getDriver() {
        return driverHolder.get();
    }
}
