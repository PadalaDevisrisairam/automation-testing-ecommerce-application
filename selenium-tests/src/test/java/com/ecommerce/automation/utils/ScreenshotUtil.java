package com.ecommerce.automation.utils;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * ScreenshotUtil captures and saves browser screenshots on test failure.
 */
public class ScreenshotUtil {

    private static final DateTimeFormatter FORMATTER =
        DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss_SSS");

    private ScreenshotUtil() {}

    /**
     * Captures a screenshot and saves it to the configured screenshot directory.
     *
     * @param driver   active WebDriver instance
     * @param testName test name used in the filename
     * @return absolute path of the saved file, or null if capture failed
     */
    public static String capture(WebDriver driver, String testName) {
        if (driver == null) return null;

        String timestamp = LocalDateTime.now().format(FORMATTER);
        String filename  = sanitize(testName) + "_" + timestamp + ".png";
        String dir       = ConfigReader.getScreenshotDir();

        File destFile = new File(dir, filename);

        try {
            File srcFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            FileUtils.copyFile(srcFile, destFile);
            System.out.println("[Screenshot] Saved: " + destFile.getAbsolutePath());
            return destFile.getAbsolutePath();
        } catch (IOException e) {
            System.err.println("[Screenshot] Failed to save: " + e.getMessage());
            return null;
        }
    }

    /**
     * Returns the screenshot as a raw byte array (useful for embedding in Allure/Extent reports).
     */
    public static byte[] captureAsBytes(WebDriver driver) {
        if (driver == null) return new byte[0];
        try {
            return ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
        } catch (Exception e) {
            System.err.println("[Screenshot] captureAsBytes failed: " + e.getMessage());
            return new byte[0];
        }
    }

    // Replace characters that are invalid in file names
    private static String sanitize(String name) {
        return name.replaceAll("[^a-zA-Z0-9_\\-]", "_");
    }
}
