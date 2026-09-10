package com.ecommerce.automation.base;

import com.ecommerce.automation.utils.ConfigReader;
import com.ecommerce.automation.utils.DriverFactory;
import com.ecommerce.automation.utils.ScreenshotUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInfo;
import org.openqa.selenium.WebDriver;

/**
 * BaseJUnitTest — inherited by all JUnit 5 test classes.
 *
 * Lifecycle:
 *   @BeforeEach  → creates a fresh WebDriver
 *   @AfterEach   → takes screenshot on failure, then quits driver
 *
 * JUnit 5 does not have a built-in ITestResult equivalent;
 * failure detection is done via a custom TestWatcher extension defined
 * in FailureScreenshotExtension, applied to each subclass via @ExtendWith.
 */
public abstract class BaseJUnitTest {

    protected WebDriver driver;
    protected static final String BASE_URL = ConfigReader.getBaseUrl();

    @BeforeEach
    public void setUp(TestInfo testInfo) {
        driver = DriverFactory.createDriver();
    }

    @AfterEach
    public void tearDown(TestInfo testInfo) {
        if (driver != null) {
            driver.quit();
            driver = null;
        }
    }

    /** Called by FailureScreenshotExtension when a test fails. */
    public void captureScreenshot(String testName) {
        if (driver != null && ConfigReader.screenshotOnFail()) {
            ScreenshotUtil.capture(driver, testName);
        }
    }
}
