package com.ecommerce.automation.testng;

import com.ecommerce.automation.base.BaseTest;
import com.ecommerce.automation.pages.LoginPage;
import com.ecommerce.automation.utils.TestData;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * TestNG tests for the Login page.
 *
 * Covers:
 *   - Page loads correctly
 *   - Successful login redirects to /products
 *   - Empty fields shows validation error
 *   - Wrong credentials shows server error
 *   - Logout returns to /login
 */
public class LoginTest extends BaseTest {

    private LoginPage loginPage;

    @BeforeMethod
    public void openLoginPage() {
        loginPage = new LoginPage(getDriver()).open(BASE_URL);
    }

    // -------------------------------------------------------------------------
    // Smoke
    // -------------------------------------------------------------------------

    @Test(description = "Login page loads with all required elements", groups = {"smoke"})
    public void testLoginPageLoads() {
        Assert.assertTrue(loginPage.isLoaded(), "Login page should display email, password and submit button");
        Assert.assertTrue(loginPage.getPageTitle().contains("ShopPOC"),
            "Page title should contain 'ShopPOC'");
    }

    @Test(description = "Login hint shows demo credentials", groups = {"smoke"})
    public void testLoginHintVisible() {
        String hint = loginPage.getHintText();
        Assert.assertTrue(hint.contains("test@example.com"), "Hint should show demo email");
        Assert.assertTrue(hint.contains("password123"), "Hint should show demo password");
    }

    // -------------------------------------------------------------------------
    // Positive
    // -------------------------------------------------------------------------

    @Test(description = "Valid credentials redirect to /products", groups = {"regression", "smoke"})
    public void testSuccessfulLogin() {
        loginPage.loginSuccessfully(TestData.VALID_EMAIL, TestData.VALID_PASSWORD);
        Assert.assertTrue(getDriver().getCurrentUrl().contains(TestData.PRODUCTS_PAGE_URL),
            "Should redirect to /products after successful login");
    }

    // -------------------------------------------------------------------------
    // Negative
    // -------------------------------------------------------------------------

    @Test(description = "Empty form submission shows client-side error", groups = {"regression"})
    public void testEmptyFieldsShowError() {
        loginPage.submitEmpty();
        Assert.assertTrue(loginPage.isErrorDisplayed(), "Error message should be visible");
        Assert.assertEquals(loginPage.getErrorMessage(), TestData.EMPTY_FIELDS_ERROR);
    }

    @Test(description = "Wrong password shows server-side error", groups = {"regression"})
    public void testInvalidPasswordShowsError() {
        loginPage.loginWith(TestData.VALID_EMAIL, TestData.INVALID_PASSWORD);
        Assert.assertTrue(loginPage.isErrorDisplayed(), "Error message should appear for wrong password");
        Assert.assertEquals(loginPage.getErrorMessage(), TestData.INVALID_CREDS_ERROR);
    }

    @Test(description = "Wrong email shows server-side error", groups = {"regression"})
    public void testInvalidEmailShowsError() {
        loginPage.loginWith(TestData.INVALID_EMAIL, TestData.VALID_PASSWORD);
        Assert.assertTrue(loginPage.isErrorDisplayed(), "Error message should appear for unknown email");
        Assert.assertEquals(loginPage.getErrorMessage(), TestData.INVALID_CREDS_ERROR);
    }

    @Test(description = "Both fields wrong shows server-side error", groups = {"regression"})
    public void testBothFieldsInvalidShowsError() {
        loginPage.loginWith(TestData.INVALID_EMAIL, TestData.INVALID_PASSWORD);
        Assert.assertTrue(loginPage.isErrorDisplayed());
        Assert.assertEquals(loginPage.getErrorMessage(), TestData.INVALID_CREDS_ERROR);
    }

    // -------------------------------------------------------------------------
    // Data-driven (DataProvider)
    // -------------------------------------------------------------------------

    @DataProvider(name = "invalidCredentials")
    public Object[][] invalidCredentials() {
        return new Object[][] {
            { "notanemail",             "password123",   TestData.INVALID_CREDS_ERROR },
            { TestData.INVALID_EMAIL,   "short",         TestData.INVALID_CREDS_ERROR },
            { TestData.INVALID_EMAIL,   TestData.INVALID_PASSWORD, TestData.INVALID_CREDS_ERROR },
        };
    }

    @Test(dataProvider = "invalidCredentials",
          description = "Various invalid credentials all show appropriate errors",
          groups = {"regression"})
    public void testInvalidCredentialVariants(String email, String password, String expectedError) {
        loginPage.loginWith(email, password);
        Assert.assertTrue(loginPage.isErrorDisplayed(),
            "Error should be shown for email=" + email);
        Assert.assertEquals(loginPage.getErrorMessage(), expectedError);
    }

    // -------------------------------------------------------------------------
    // Logout flow (needs a successful login first)
    // -------------------------------------------------------------------------

    @Test(description = "Logout from products page returns to login", groups = {"regression"})
    public void testLogout() {
        loginPage.loginSuccessfully(TestData.VALID_EMAIL, TestData.VALID_PASSWORD);
        Assert.assertTrue(getDriver().getCurrentUrl().contains("/products"));

        // Use navbar logout
        getDriver().findElement(org.openqa.selenium.By.cssSelector(".navbar .btn-outline")).click();
        new com.ecommerce.automation.pages.BasePage(getDriver()) {}.waitForUrlContains("/login");

        Assert.assertTrue(getDriver().getCurrentUrl().contains("/login"),
            "After logout should redirect to /login");
    }
}
