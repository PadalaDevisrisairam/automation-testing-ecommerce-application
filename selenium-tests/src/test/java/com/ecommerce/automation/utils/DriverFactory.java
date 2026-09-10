package com.ecommerce.automation.utils;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;

import java.time.Duration;

/**
 * DriverFactory creates and configures a WebDriver instance.
 * Uses WebDriverManager to automatically download the correct browser driver.
 *
 * Supports: chrome | firefox | edge
 * Respects headless and timeout settings from ConfigReader.
 */
public class DriverFactory {

    private DriverFactory() {}

    /**
     * Creates a browser driver based on the config.properties "browser" value.
     * Passes -Dbrowser=<name> on the command line to override at runtime.
     */
    public static WebDriver createDriver() {
        String browser = ConfigReader.getBrowser().toLowerCase();

        WebDriver driver = switch (browser) {
            case "firefox" -> createFirefox();
            case "edge"    -> createEdge();
            default        -> createChrome();   // chrome is the default
        };

        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(ConfigReader.getImplicitWait()));
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(ConfigReader.getPageLoadTimeout()));
        driver.manage().window().maximize();

        return driver;
    }

    // ----- Chrome -----------------------------------------------------------

    private static WebDriver createChrome() {
        WebDriverManager.chromedriver().setup();
        ChromeOptions opts = new ChromeOptions();
        if (ConfigReader.isHeadless()) {
            opts.addArguments("--headless=new");
        }
        opts.addArguments(
            "--no-sandbox",
            "--disable-dev-shm-usage",
            "--disable-gpu",
            "--window-size=1920,1080",
            "--remote-allow-origins=*"
        );
        return new ChromeDriver(opts);
    }

    // ----- Firefox ----------------------------------------------------------

    private static WebDriver createFirefox() {
        WebDriverManager.firefoxdriver().setup();
        FirefoxOptions opts = new FirefoxOptions();
        if (ConfigReader.isHeadless()) {
            opts.addArguments("--headless");
        }
        return new FirefoxDriver(opts);
    }

    // ----- Edge -------------------------------------------------------------

    private static WebDriver createEdge() {
        WebDriverManager.edgedriver().setup();
        EdgeOptions opts = new EdgeOptions();
        if (ConfigReader.isHeadless()) {
            opts.addArguments("--headless=new");
        }
        opts.addArguments(
            "--no-sandbox",
            "--disable-dev-shm-usage",
            "--window-size=1920,1080"
        );
        return new EdgeDriver(opts);
    }
}
