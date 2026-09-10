package com.ecommerce.automation.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;

/**
 * CartPage mirrors the Cart.jsx component.
 *
 * Key data-testid attributes in the React source:
 *   data-testid="cart-item"                  — each cart row
 *   data-testid="remove-from-cart-button"    — remove button per row
 *   data-testid="cart-total"                 — total price span
 *
 * Navigation link from Navbar:
 *   data-testid="cart-link"
 */
public class CartPage extends BasePage {

    // ----- Locators ----------------------------------------------------------
    private static final By CART_ITEMS          = By.cssSelector("[data-testid='cart-item']");
    private static final By REMOVE_BUTTONS      = By.cssSelector("[data-testid='remove-from-cart-button']");
    private static final By CART_TOTAL          = By.cssSelector("[data-testid='cart-total']");
    private static final By CART_LINK           = By.cssSelector("[data-testid='cart-link']");
    private static final By EMPTY_CART_MSG      = By.cssSelector(".empty-state p");
    private static final By BROWSE_BTN          = By.cssSelector(".empty-state .btn");
    private static final By CHECKOUT_BTN        = By.cssSelector(".btn-primary:not(.btn-full ~ .btn-primary)");
    private static final By ITEM_NAMES          = By.cssSelector("[data-testid='cart-item'] .cart-item-name");
    private static final By ITEM_PRICES         = By.cssSelector("[data-testid='cart-item'] .cart-item-price");
    private static final By ITEM_SUBTOTALS      = By.cssSelector("[data-testid='cart-item'] .cart-item-subtotal");
    private static final By LOADING             = By.cssSelector(".loading");
    private static final By PAGE_HEADING        = By.cssSelector(".page-header h1");

    // ----- Constructor -------------------------------------------------------
    public CartPage(WebDriver driver) {
        super(driver);
    }

    // ----- Navigation --------------------------------------------------------

    public CartPage open(String baseUrl) {
        driver.get(baseUrl + "/cart");
        waitForPageLoad();
        return this;
    }

    /** Clicks the Cart icon in the Navbar to navigate to the cart. */
    public CartPage clickCartLink() {
        click(CART_LINK);
        waitForUrlContains("/cart");
        waitForPageLoad();
        return this;
    }

    private void waitForPageLoad() {
        wait.until(d -> !isDisplayed(LOADING));
    }

    // ----- Page State Queries ------------------------------------------------

    public boolean isLoaded() {
        return getCurrentUrl().contains("/cart") && !isDisplayed(LOADING);
    }

    public boolean isCartEmpty() {
        return isDisplayed(EMPTY_CART_MSG);
    }

    public String getEmptyCartMessage() {
        return getText(EMPTY_CART_MSG);
    }

    public int getCartItemCount() {
        return countElements(CART_ITEMS);
    }

    public List<WebElement> getCartItems() {
        return driver.findElements(CART_ITEMS);
    }

    /**
     * Returns the item name at position index (0-based).
     */
    public String getItemName(int index) {
        List<WebElement> names = driver.findElements(ITEM_NAMES);
        return names.get(index).getText();
    }

    /**
     * Returns the raw subtotal text at position index (0-based), e.g. "$29.99".
     */
    public String getItemSubtotal(int index) {
        List<WebElement> subtotals = driver.findElements(ITEM_SUBTOTALS);
        return subtotals.get(index).getText();
    }

    /**
     * Returns the cart total string shown at the bottom, e.g. "$89.97".
     */
    public String getCartTotal() {
        return getText(CART_TOTAL);
    }

    /**
     * Parses the cart total to a double for numeric assertions.
     */
    public double getCartTotalAsDouble() {
        return Double.parseDouble(getCartTotal().replace("$", "").trim());
    }

    public boolean isCartTotalDisplayed() {
        return isDisplayed(CART_TOTAL);
    }

    public String getPageHeading() {
        return getText(PAGE_HEADING);
    }

    // ----- Actions -----------------------------------------------------------

    /**
     * Clicks Remove on the first cart item.
     */
    public CartPage removeFirstItem() {
        click(REMOVE_BUTTONS);
        waitForPageLoad();
        return this;
    }

    /**
     * Clicks Remove on the Nth cart item (0-based).
     */
    public CartPage removeItem(int index) {
        List<WebElement> buttons = driver.findElements(REMOVE_BUTTONS);
        buttons.get(index).click();
        waitForPageLoad();
        return this;
    }

    /**
     * Removes all items one by one until the cart is empty.
     */
    public CartPage removeAllItems() {
        while (countElements(REMOVE_BUTTONS) > 0) {
            removeFirstItem();
        }
        return this;
    }

    /**
     * Clicks "Browse Products" in the empty cart state.
     */
    public ProductsPage browseProducts() {
        click(BROWSE_BTN);
        waitForUrlContains("/products");
        return new ProductsPage(driver);
    }
}
