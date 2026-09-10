package com.ecommerce.automation.base;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestWatcher;

import java.util.Optional;

/**
 * JUnit 5 TestWatcher extension that captures a screenshot when a test fails.
 *
 * Usage — add to any JUnit test class:
 *   @ExtendWith(FailureScreenshotExtension.class)
 */
public class FailureScreenshotExtension implements TestWatcher {

    @Override
    public void testFailed(ExtensionContext context, Throwable cause) {
        // Retrieve the test instance if it is a BaseJUnitTest subclass
        Optional<Object> testInstance = context.getTestInstance();
        testInstance.ifPresent(instance -> {
            if (instance instanceof BaseJUnitTest base) {
                String testName = context.getDisplayName();
                base.captureScreenshot(testName);
            }
        });
    }
}
