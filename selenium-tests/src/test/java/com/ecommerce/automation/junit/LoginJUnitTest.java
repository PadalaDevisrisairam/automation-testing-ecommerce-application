package com.ecommerce.automation.junit;

import com.ecommerce.automation.base.BaseJUnitTest;
import com.ecommerce.automation.base.FailureScreenshotExtension;
import com.ecommerce.automation.pages.LoginPage;
import com.ecommerce.automation.utils.TestData;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * JUnit 5 tests for the Login page.
 * Mirrors LoginTest.java (TestNG) — same coverage, JUnit 5 style.
 */
@ExtendWith(FailureScreenshotExtension.class)
@TestMethodOrder(MethodOrderer.DisplayName.class)
@DisplayName("Login Page — JUnit 5")
class LoginJUnitTest extends BaseJUnitTest {

    private LoginPage loginPage;

    @BeforeEach
    void openLogin(TestInfo info) {
        loginPage = new LoginPage(driver).open(BASE_URL);
    }

    // -------------------------------------------------------------------------
    // Smoke
    // -------------------------------------------------------------------------

    @Test
    @Tag("smoke")
    @DisplayName("Page loads with all required elements")
    void pageLoads() {
        assertThat(loginPage.isLoaded())
            .as("Email, password and submit button should all be visible")
            .isTrue();
        assertThat(loginPage.getPageTitle()).contains("ShopPOC");
    }

    @Test
    @Tag("smoke")
    @DisplayName("Login hint shows demo credentials")
    void hintShowsDemoCredentials() {
        String hint = loginPage.getHintText();
        assertThat(hint).contains("test@example.com").contains("password123");
    }

    // -------------------------------------------------------------------------
    // Positive
    // -------------------------------------------------------------------------

    @Test
    @Tag("smoke")
    @Tag("regression")
    @DisplayName("Valid credentials redirect to /products")
    void successfulLoginRedirects() {
        loginPage.loginSuccessfully(TestData.VALID_EMAIL, TestData.VALID_PASSWORD);
        assertThat(driver.getCurrentUrl()).contains(TestData.PRODUCTS_PAGE_URL);
    }

    // -------------------------------------------------------------------------
    // Negative
    // -------------------------------------------------------------------------

    @Test
    @Tag("regression")
    @DisplayName("Empty form shows client-side validation error")
    void emptyFormShowsError() {
        loginPage.submitEmpty();
        assertThat(loginPage.isErrorDisplayed()).isTrue();
        assertThat(loginPage.getErrorMessage()).isEqualTo(TestData.EMPTY_FIELDS_ERROR);
    }

    @Test
    @Tag("regression")
    @DisplayName("Wrong password shows server-side error")
    void wrongPasswordShowsError() {
        loginPage.loginWith(TestData.VALID_EMAIL, TestData.INVALID_PASSWORD);
        assertThat(loginPage.isErrorDisplayed()).isTrue();
        assertThat(loginPage.getErrorMessage()).isEqualTo(TestData.INVALID_CREDS_ERROR);
    }

    @Test
    @Tag("regression")
    @DisplayName("Unknown email shows server-side error")
    void unknownEmailShowsError() {
        loginPage.loginWith(TestData.INVALID_EMAIL, TestData.VALID_PASSWORD);
        assertThat(loginPage.isErrorDisplayed()).isTrue();
        assertThat(loginPage.getErrorMessage()).isEqualTo(TestData.INVALID_CREDS_ERROR);
    }

    // -------------------------------------------------------------------------
    // Parameterized
    // -------------------------------------------------------------------------

    @ParameterizedTest(name = "email={0} / password={1} → error: {2}")
    @CsvSource({
        "notanemail,         password123,    Invalid email or password.",
        "wrong@example.com,  short,          Invalid email or password.",
        "wrong@example.com,  wrongpassword,  Invalid email or password."
    })
    @Tag("regression")
    @DisplayName("Invalid credential variants all produce errors")
    void invalidCredentialVariants(String email, String password, String expectedError) {
        loginPage.loginWith(email.trim(), password.trim());
        assertThat(loginPage.isErrorDisplayed())
            .as("Error should show for email=%s", email)
            .isTrue();
        assertThat(loginPage.getErrorMessage()).isEqualTo(expectedError.trim());
    }

    // -------------------------------------------------------------------------
    // Logout
    // -------------------------------------------------------------------------

    @Test
    @Tag("regression")
    @DisplayName("Logout from products page returns to /login")
    void logoutReturnsToLogin() {
        loginPage.loginSuccessfully(TestData.VALID_EMAIL, TestData.VALID_PASSWORD);
        assertThat(driver.getCurrentUrl()).contains("/products");

        driver.findElement(org.openqa.selenium.By.cssSelector(".navbar .btn-outline")).click();
        // Wait for redirect
        new org.openqa.selenium.support.ui.WebDriverWait(driver, java.time.Duration.ofSeconds(10))
            .until(d -> d.getCurrentUrl().contains("/login"));

        assertThat(driver.getCurrentUrl()).contains("/login");
    }
}
