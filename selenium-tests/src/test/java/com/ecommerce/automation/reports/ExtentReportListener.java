package com.ecommerce.automation.reports;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;
import com.ecommerce.automation.utils.ConfigReader;
import com.ecommerce.automation.utils.ScreenshotUtil;
import org.openqa.selenium.WebDriver;
import org.testng.*;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * ExtentReportListener is a TestNG ITestListener that builds an HTML report
 * using ExtentReports Spark Reporter.
 *
 * Report location: target/reports/ExtentReport_<timestamp>.html
 *
 * Register in testng XML suites:
 *   <listener class-name="com.ecommerce.automation.reports.ExtentReportListener"/>
 */
public class ExtentReportListener implements ITestListener, ISuiteListener {

    private static ExtentReports         extent;
    private static ThreadLocal<ExtentTest> testHolder = new ThreadLocal<>();

    // ----- Suite lifecycle --------------------------------------------------

    @Override
    public void onStart(ISuite suite) {
        String timestamp  = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String reportDir  = ConfigReader.getReportDir();
        String reportPath = reportDir + File.separator + "ExtentReport_" + timestamp + ".html";

        new File(reportDir).mkdirs();

        ExtentSparkReporter spark = new ExtentSparkReporter(reportPath);
        spark.config().setDocumentTitle("ECommerce Automation Report");
        spark.config().setReportName("Selenium Test Results");
        spark.config().setTheme(Theme.DARK);
        spark.config().setTimeStampFormat("yyyy-MM-dd HH:mm:ss");

        extent = new ExtentReports();
        extent.attachReporter(spark);
        extent.setSystemInfo("OS",       System.getProperty("os.name"));
        extent.setSystemInfo("Java",     System.getProperty("java.version"));
        extent.setSystemInfo("Browser",  ConfigReader.getBrowser());
        extent.setSystemInfo("Base URL", ConfigReader.getBaseUrl());
    }

    @Override
    public void onFinish(ISuite suite) {
        if (extent != null) {
            extent.flush();
        }
    }

    // ----- Test lifecycle ---------------------------------------------------

    @Override
    public void onTestStart(ITestResult result) {
        ExtentTest test = extent.createTest(
            result.getTestClass().getName() + " — " + result.getName(),
            result.getMethod().getDescription()
        );
        testHolder.set(test);
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        getTest().log(Status.PASS, "Test PASSED");
    }

    @Override
    public void onTestFailure(ITestResult result) {
        ExtentTest test = getTest();
        test.log(Status.FAIL, "Test FAILED: " + result.getThrowable().getMessage());
        test.fail(result.getThrowable());

        // Attach screenshot only if the test instance is a UI (BaseTest) subclass.
        // Use reflection to avoid a hard compile-time dependency on BaseTest / WebDriver,
        // which would cause the browser driver to be initialised when this listener is
        // loaded by purely API-focused suites.
        try {
            Object instance = result.getInstance();
            Class<?> baseTestClass = Class.forName("com.ecommerce.automation.base.BaseTest");
            if (baseTestClass.isInstance(instance)) {
                WebDriver driver = (WebDriver) baseTestClass
                        .getMethod("getDriver")
                        .invoke(null);
                if (driver != null) {
                    String path = ScreenshotUtil.capture(driver, result.getName());
                    if (path != null) {
                        test.addScreenCaptureFromPath(path, "Failure Screenshot");
                    }
                }
            }
        } catch (ClassNotFoundException ignored) {
            // BaseTest not on classpath — safe to skip screenshot
        } catch (Exception e) {
            test.log(Status.WARNING, "Screenshot could not be attached: " + e.getMessage());
        }
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        getTest().log(Status.SKIP, "Test SKIPPED: " + result.getThrowable());
    }

    // ----- Helper -----------------------------------------------------------

    private ExtentTest getTest() {
        return testHolder.get();
    }
}
