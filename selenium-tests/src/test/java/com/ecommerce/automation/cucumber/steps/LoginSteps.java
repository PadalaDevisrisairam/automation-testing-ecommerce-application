package com.ecommerce.automation.cucumber.steps;

import com.ecommerce.automation.pages.LoginPage;
import com.ecommerce.automation.utils.ConfigReader;
import com.ecommerce.automation.utils.TestData;
import io.cucumber.java.en.*;
import org.openqa.selenium.By;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Step definitions for login.feature.
 */
public class LoginSteps {

    private final ScenarioContext ctx;
    private LoginPage loginPage;

    public LoginSteps(ScenarioContext ctx) {
        this.ctx = ctx;
    }

    // ----- Given ------------------------------------------------------------

    @Given("the user navigates to the login page")
    public void navigateToLoginPage() {
        loginPage = new LoginPage(ctx.getDriver())
            .open(ConfigReader.getBaseUrl());
    }

    @Given("the user is logged in with valid credentials")
    public void userIsLoggedIn() {
        loginPage = new LoginPage(ctx.getDriver())
            .open(ConfigReader.getBaseUrl());
        loginPage.loginSuccessfully(TestData.VALID_EMAIL, TestData.VALID_PASSWORD);
    }

    // ----- When -------------------------------------------------------------

    @When("the user enters email {string} and password {string}")
    public void enterCredentials(String email, String password) {
        loginPage.enterEmail(email).enterPassword(password);
    }

    @When("the user clicks the sign-in button")
    public void clickSignIn() {
        loginPage.clickLoginButton();
    }

    @When("the user clicks the logout button")
    public void clickLogout() {
        ctx.getDriver()
           .findElement(By.cssSelector(".navbar .btn-outline"))
           .click();
        new org.openqa.selenium.support.ui.WebDriverWait(
                ctx.getDriver(), java.time.Duration.ofSeconds(10))
            .until(d -> d.getCurrentUrl().contains("/login"));
    }

    // ----- Then -------------------------------------------------------------

    @Then("the email input should be visible")
    public void emailInputVisible() {
        assertThat(loginPage.isLoaded()).isTrue();
    }

    @Then("the password input should be visible")
    public void passwordInputVisible() {
        assertThat(loginPage.isLoaded()).isTrue();
    }

    @Then("the sign-in button should be visible")
    public void signInButtonVisible() {
        assertThat(loginPage.isLoaded()).isTrue();
    }

    @Then("the page title should contain {string}")
    public void pageTitleContains(String expected) {
        assertThat(loginPage.getPageTitle()).contains(expected);
    }

    @Then("the login hint should contain {string}")
    public void loginHintContains(String expected) {
        assertThat(loginPage.getHintText()).contains(expected);
    }

    @Then("the user should be redirected to the products page")
    public void redirectedToProducts() {
        assertThat(ctx.getDriver().getCurrentUrl())
            .contains(TestData.PRODUCTS_PAGE_URL);
    }

    @Then("the user should be redirected to the login page")
    public void redirectedToLogin() {
        assertThat(ctx.getDriver().getCurrentUrl())
            .contains(TestData.LOGIN_PAGE_URL);
    }

    @Then("an error message should be displayed")
    public void errorMessageDisplayed() {
        assertThat(loginPage.isErrorDisplayed())
            .as("Login error message should be visible")
            .isTrue();
    }

    @Then("the error message should read {string}")
    public void errorMessageReads(String expectedText) {
        assertThat(loginPage.getErrorMessage()).isEqualTo(expectedText);
    }
}
