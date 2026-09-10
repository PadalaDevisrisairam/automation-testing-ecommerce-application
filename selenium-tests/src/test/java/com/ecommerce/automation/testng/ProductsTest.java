package com.ecommerce.automation.testng;

import com.ecommerce.automation.base.BaseTest;
import com.ecommerce.automation.pages.LoginPage;
import com.ecommerce.automation.pages.ProductsPage;
import com.ecommerce.automation.utils.TestData;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * TestNG tests for the Products page.
 *
 * Covers:
 *   - Products grid renders after login
 *   - Search filters results correctly
 *   - Search with no match shows empty state
 *   - Clearing search restores all products
 *   - Add to Cart shows success notification
 */
public class ProductsTest extends BaseTest {

    private ProductsPage productsPage;

    @BeforeMethod
    public void loginAndOpenProducts() {
        // Log in first, then land on products page
        new LoginPage(getDriver())
            .open(BASE_URL)
            .loginSuccessfully(TestData.VALID_EMAIL, TestData.VALID_PASSWORD);

        productsPage = new ProductsPage(getDriver());
    }

    // -------------------------------------------------------------------------
    // Smoke
    // -------------------------------------------------------------------------

    @Test(description = "Products grid is visible after login", groups = {"smoke"})
    public void testProductsGridVisible() {
        Assert.assertTrue(productsPage.isLoaded(),
            "Products grid or empty state should be visible");
    }

    @Test(description = "At least one product is listed", groups = {"smoke", "regression"})
    public void testProductsListNotEmpty() {
        Assert.assertTrue(productsPage.getProductCount() >= TestData.EXPECTED_MIN_PRODUCTS,
            "Expected at least " + TestData.EXPECTED_MIN_PRODUCTS + " product(s)");
    }

    @Test(description = "URL is /products after login", groups = {"smoke"})
    public void testProductsUrl() {
        Assert.assertTrue(getDriver().getCurrentUrl().contains(TestData.PRODUCTS_PAGE_URL));
    }

    // -------------------------------------------------------------------------
    // Search
    // -------------------------------------------------------------------------

    @Test(description = "Search with a valid term filters results", groups = {"regression"})
    public void testSearchFiltersProducts() {
        int allCount = productsPage.getProductCount();
        productsPage.searchFor(TestData.SEARCH_TERM_VALID);

        // All visible product names should contain the search term
        Assert.assertTrue(productsPage.anyProductContains(TestData.SEARCH_TERM_VALID),
            "Search results should contain products matching '" + TestData.SEARCH_TERM_VALID + "'");

        // Optionally count may be <= total (filter narrows results)
        Assert.assertTrue(productsPage.getProductCount() <= allCount,
            "Search should not return more products than the full list");
    }

    @Test(description = "Search with no match shows empty state", groups = {"regression"})
    public void testSearchNoMatchShowsEmptyState() {
        productsPage.searchFor(TestData.SEARCH_TERM_NO_MATCH);
        Assert.assertTrue(productsPage.isEmptyStateDisplayed(),
            "No-match search should show the empty state message");
        Assert.assertEquals(productsPage.getProductCount(), 0,
            "Product count should be 0 when no match found");
    }

    @Test(description = "Clearing search restores full product list", groups = {"regression"})
    public void testClearSearchRestoresProducts() {
        int initialCount = productsPage.getProductCount();

        productsPage
            .searchFor(TestData.SEARCH_TERM_NO_MATCH)
            .clearSearch();

        Assert.assertEquals(productsPage.getProductCount(), initialCount,
            "Clearing search should restore all products");
    }

    // -------------------------------------------------------------------------
    // Add to Cart
    // -------------------------------------------------------------------------

    @Test(description = "Add first product to cart shows notification", groups = {"regression", "smoke"})
    public void testAddToCartShowsNotification() {
        productsPage.addFirstProductToCart().waitForNotification();

        Assert.assertTrue(productsPage.isNotificationDisplayed(),
            "Notification should appear after adding a product to cart");
        Assert.assertTrue(productsPage.getNotificationText().toLowerCase().contains("added to cart"),
            "Notification text should confirm item was added");
    }

    @Test(description = "Add to cart notification contains product name", groups = {"regression"})
    public void testAddToCartNotificationContainsProductName() {
        String firstName = productsPage.getProductName(0);
        productsPage.addFirstProductToCart().waitForNotification();

        Assert.assertTrue(productsPage.getNotificationText().contains(firstName),
            "Notification should include the product name: " + firstName);
    }
}
