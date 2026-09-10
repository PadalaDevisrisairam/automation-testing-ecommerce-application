package com.ecommerce.automation.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * LoginPage mirrors the Login.jsx component.
 *
 * Locators are driven by data-testid attributes present in the React source:
 *   data-testid="email-input"
 *   data-testid="password-input"
 *   data-testid="login-button"
 *   data-testid="login-error"
 */
public class LoginPage extends BasePage {

    // ----- Locators ----------------------------------------------------------
    private static final By EMAIL_INPUT    = By.cssSelector("[data-testid='email-input']");
    private static final By PASSWORD_INPUT = By.cssSelector("[data-testid='password-input']");
    private static final By LOGIN_BUTTON   = By.cssSelector("[data-testid='login-button']");
    private static final By ERROR_MESSAGE  = By.cssSelector("[data-testid='login-error']");
    private static final By PAGE_TITLE     = By.cssSelector(".login-title");
    private static final By LOGIN_HINT     = By.cssSelector(".login-hint");

    // ----- Constructor -------------------------------------------------------
    public LoginPage(WebDriver driver) {
        super(driver);
    }

    // ----- Navigation --------------------------------------------------------

    public LoginPage open(String baseUrl) {
        driver.get(baseUrl + "/login");
        waitForVisible(EMAIL_INPUT);
        return this;
    }

    // ----- Page State Queries ------------------------------------------------

    public boolean isLoaded() {
        return isDisplayed(EMAIL_INPUT) && isDisplayed(PASSWORD_INPUT) && isDisplayed(LOGIN_BUTTON);
    }

    public String getPageTitle() {
        return getText(PAGE_TITLE);
    }

    public boolean isErrorDisplayed() {
        return isDisplayed(ERROR_MESSAGE);
    }

    public String getErrorMessage() {
        return getText(ERROR_MESSAGE);
    }

    public boolean isLoginButtonDisabled() {
        return Boolean.parseBoolean(
            driver.findElement(LOGIN_BUTTON).getAttribute("disabled")
        );
    }

    public String getHintText() {
        return getText(LOGIN_HINT);
    }

    // ----- Actions -----------------------------------------------------------

    public LoginPage enterEmail(String email) {
        type(EMAIL_INPUT, email);
        return this;
    }

    public LoginPage enterPassword(String password) {
        type(PASSWORD_INPUT, password);
        return this;
    }

    public LoginPage clickLoginButton() {
        click(LOGIN_BUTTON);
        return this;
    }

    /**
     * Convenience method: fills credentials and clicks Sign In.
     * Returns this page so callers can chain assertions or wait for navigation.
     */
    public LoginPage loginWith(String email, String password) {
        enterEmail(email);
        enterPassword(password);
        clickLoginButton();
        return this;
    }

    /**
     * Performs a successful login and waits for redirect to /products.
     */
    public void loginSuccessfully(String email, String password) {
        loginWith(email, password);
        waitForUrlContains("/products");
    }

    /**
     * Submits the form with empty fields to trigger client-side validation.
     */
    public LoginPage submitEmpty() {
        clickLoginButton();
        return this;
    }
}
