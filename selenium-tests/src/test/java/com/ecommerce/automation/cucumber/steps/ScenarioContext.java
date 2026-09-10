package com.ecommerce.automation.cucumber.steps;

import org.openqa.selenium.WebDriver;

/**
 * ScenarioContext is a Cucumber PicoContainer-managed shared state object.
 *
 * Cucumber instantiates one instance per scenario and injects it into every
 * step definition class that declares it as a constructor parameter.
 * This allows multiple step classes to share the same WebDriver and scenario data
 * without using static fields.
 */
public class ScenarioContext {

    private WebDriver driver;

    // Scenario-level data shared between step classes
    private String notedProductName;
    private int    cartItemCountBefore;

    // ----- Driver -----------------------------------------------------------

    public WebDriver getDriver() {
        return driver;
    }

    public void setDriver(WebDriver driver) {
        this.driver = driver;
    }

    // ----- Shared scenario data ---------------------------------------------

    public String getNotedProductName() {
        return notedProductName;
    }

    public void setNotedProductName(String name) {
        this.notedProductName = name;
    }

    public int getCartItemCountBefore() {
        return cartItemCountBefore;
    }

    public void setCartItemCountBefore(int count) {
        this.cartItemCountBefore = count;
    }
}
