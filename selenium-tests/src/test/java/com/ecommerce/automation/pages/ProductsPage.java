package com.ecommerce.automation.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;

/**
 * ProductsPage mirrors the Products.jsx component + SearchBar + ProductCard.
 *
 * Key data-testid attributes in the React source:
 *   data-testid="products-grid"          — the card grid container
 *
 * Other selectors rely on stable CSS classes:
 *   .search-input                         — SearchBar input
 *   .product-card                         — each product card
 *   .product-name                         — product name heading
 *   .product-price                        — price text
 *   .btn (within .product-card)           — "Add to Cart" button
 *   .notification                         — success/error toast
 *   .loading                              — loading spinner text
 *   .empty-state                          — no-results message
 */
public class ProductsPage extends BasePage {

    // ----- Locators ----------------------------------------------------------
    private static final By PRODUCTS_GRID    = By.cssSelector("[data-testid='products-grid']");
    private static final By SEARCH_INPUT     = By.cssSelector(".search-input");
    private static final By PRODUCT_CARDS    = By.cssSelector(".product-card");
    private static final By PRODUCT_NAMES    = By.cssSelector(".product-card .product-name");
    private static final By ADD_TO_CART_BTNS = By.cssSelector(".product-card .btn");
    private static final By NOTIFICATION     = By.cssSelector(".notification");
    private static final By LOADING          = By.cssSelector(".loading");
    private static final By EMPTY_STATE      = By.cssSelector(".empty-state");

    // ----- Constructor -------------------------------------------------------
    public ProductsPage(WebDriver driver) {
        super(driver);
    }

    // ----- Navigation --------------------------------------------------------

    public ProductsPage open(String baseUrl) {
        driver.get(baseUrl + "/products");
        waitForPageLoad();
        return this;
    }

    private void waitForPageLoad() {
        // Wait until loading spinner disappears OR grid appears OR empty state shows
        wait.until(d ->
            isDisplayed(PRODUCTS_GRID) ||
            isDisplayed(EMPTY_STATE)   ||
            !isDisplayed(LOADING)
        );
    }

    // ----- Page State Queries ------------------------------------------------

    public boolean isLoaded() {
        return isDisplayed(PRODUCTS_GRID) || isDisplayed(EMPTY_STATE);
    }

    public int getProductCount() {
        return countElements(PRODUCT_CARDS);
    }

    public List<WebElement> getAllProductCards() {
        return driver.findElements(PRODUCT_CARDS);
    }

    public List<WebElement> getAllProductNames() {
        return driver.findElements(PRODUCT_NAMES);
    }

    public boolean isNotificationDisplayed() {
        return isDisplayed(NOTIFICATION);
    }

    public String getNotificationText() {
        return getText(NOTIFICATION);
    }

    public boolean isEmptyStateDisplayed() {
        return isDisplayed(EMPTY_STATE);
    }

    public boolean isLoadingDisplayed() {
        return isDisplayed(LOADING);
    }

    /**
     * Returns the name text of the Nth product card (0-indexed).
     */
    public String getProductName(int index) {
        List<WebElement> names = driver.findElements(PRODUCT_NAMES);
        if (index >= names.size()) {
            throw new IndexOutOfBoundsException("Product index " + index + " out of range. Found: " + names.size());
        }
        return names.get(index).getText();
    }

    /**
     * Checks if any visible product name contains the given text (case-insensitive).
     */
    public boolean anyProductContains(String text) {
        return driver.findElements(PRODUCT_NAMES).stream()
            .anyMatch(el -> el.getText().toLowerCase().contains(text.toLowerCase()));
    }

    // ----- Actions -----------------------------------------------------------

    public ProductsPage searchFor(String term) {
        type(SEARCH_INPUT, term);
        // Allow debounce (300 ms in app) to settle before assertions
        try { Thread.sleep(500); } catch (InterruptedException ignored) {}
        waitForPageLoad();
        return this;
    }

    public ProductsPage clearSearch() {
        WebElement input = waitForVisible(SEARCH_INPUT);
        input.clear();
        try { Thread.sleep(500); } catch (InterruptedException ignored) {}
        waitForPageLoad();
        return this;
    }

    /**
     * Clicks "Add to Cart" on the first product card.
     */
    public ProductsPage addFirstProductToCart() {
        click(ADD_TO_CART_BTNS);
        return this;
    }

    /**
     * Clicks "Add to Cart" on the Nth product card (0-indexed).
     */
    public ProductsPage addProductToCart(int index) {
        List<WebElement> buttons = driver.findElements(ADD_TO_CART_BTNS);
        if (index >= buttons.size()) {
            throw new IndexOutOfBoundsException("Button index " + index + " out of range. Found: " + buttons.size());
        }
        buttons.get(index).click();
        return this;
    }

    /**
     * Waits for the success notification to appear after adding to cart.
     */
    public ProductsPage waitForNotification() {
        waitForVisible(NOTIFICATION);
        return this;
    }
}
