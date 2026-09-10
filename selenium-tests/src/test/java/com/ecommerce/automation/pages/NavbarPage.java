package com.ecommerce.automation.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * NavbarPage encapsulates interactions with the top navigation bar (Navbar.jsx).
 *
 * data-testid attributes:
 *   data-testid="cart-link"   — Cart icon link
 *
 * CSS classes used:
 *   .navbar-brand a           — ShopPOC brand/home link
 *   .navbar-links a           — Products link
 *   .navbar-user              — logged-in user email display
 *   .btn-outline (in navbar)  — Logout button
 */
public class NavbarPage extends BasePage {

    // ----- Locators ----------------------------------------------------------
    private static final By BRAND_LINK      = By.cssSelector(".navbar-brand a");
    private static final By PRODUCTS_LINK   = By.cssSelector(".navbar-links a[href='/products']");
    private static final By CART_LINK       = By.cssSelector("[data-testid='cart-link']");
    private static final By USER_DISPLAY    = By.cssSelector(".navbar-user");
    private static final By LOGOUT_BUTTON   = By.cssSelector(".navbar .btn-outline");

    // ----- Constructor -------------------------------------------------------
    public NavbarPage(WebDriver driver) {
        super(driver);
    }

    // ----- Page State Queries ------------------------------------------------

    public boolean isUserLoggedIn() {
        return isDisplayed(USER_DISPLAY);
    }

    public String getLoggedInUserEmail() {
        return getText(USER_DISPLAY).replace("👤 ", "").trim();
    }

    public boolean isCartLinkDisplayed() {
        return isDisplayed(CART_LINK);
    }

    // ----- Actions -----------------------------------------------------------

    public ProductsPage goToProducts() {
        click(BRAND_LINK);
        waitForUrlContains("/products");
        return new ProductsPage(driver);
    }

    public CartPage goToCart() {
        click(CART_LINK);
        waitForUrlContains("/cart");
        return new CartPage(driver);
    }

    public LoginPage logout() {
        click(LOGOUT_BUTTON);
        waitForUrlContains("/login");
        return new LoginPage(driver);
    }
}
