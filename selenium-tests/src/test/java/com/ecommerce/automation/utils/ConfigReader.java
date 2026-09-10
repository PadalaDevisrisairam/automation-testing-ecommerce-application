package com.ecommerce.automation.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * ConfigReader loads test configuration from config.properties.
 * Singleton — loaded once and cached for the test session.
 */
public class ConfigReader {

    private static final String CONFIG_FILE = "config.properties";
    private static final Properties props = new Properties();

    static {
        try (InputStream is = ConfigReader.class
                .getClassLoader()
                .getResourceAsStream(CONFIG_FILE)) {
            if (is == null) {
                throw new RuntimeException("config.properties not found on classpath");
            }
            props.load(is);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load " + CONFIG_FILE, e);
        }
    }

    private ConfigReader() {}

    // ----- Generic getter ---------------------------------------------------

    public static String get(String key) {
        String value = System.getProperty(key, props.getProperty(key));
        if (value == null) {
            throw new RuntimeException("Missing config key: " + key);
        }
        return value.trim();
    }

    public static String get(String key, String defaultValue) {
        return System.getProperty(key, props.getProperty(key, defaultValue)).trim();
    }

    // ----- Convenience getters ----------------------------------------------

    public static String getBaseUrl()         { return get("base.url"); }
    public static String getApiUrl()          { return get("api.url"); }
    public static String getBrowser()         { return get("browser", "chrome"); }
    public static boolean isHeadless()        { return Boolean.parseBoolean(get("headless", "false")); }
    public static int getImplicitWait()       { return Integer.parseInt(get("implicit.wait", "10")); }
    public static int getPageLoadTimeout()    { return Integer.parseInt(get("page.load.timeout", "30")); }
    public static boolean screenshotOnFail()  { return Boolean.parseBoolean(get("screenshot.on.failure", "true")); }
    public static String getScreenshotDir()   { return get("screenshot.dir", "target/screenshots"); }
    public static String getReportDir()       { return get("report.dir", "target/reports"); }

    public static String getValidEmail()      { return get("valid.email"); }
    public static String getValidPassword()   { return get("valid.password"); }
    public static String getInvalidEmail()    { return get("invalid.email"); }
    public static String getInvalidPassword() { return get("invalid.password"); }
}
