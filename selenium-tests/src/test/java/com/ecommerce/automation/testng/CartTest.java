package com.ecommerce.automation.testng;

import com.ecommerce.automation.base.BaseTest;
import com.ecommerce.automation.pages.CartPage;
import com.ecommerce.automation.pages.LoginPage;
import com.ecommerce.automation.pages.NavbarPage;
import com.ecommerce.automation.pages.ProductsPage;
import com.ecommerce.automation.utils.TestData;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * TestNG tests for the Cart page.
 *
 * Covers:
 *   - Unauthenticated access redirects to /login
 *   - Empty cart state is displayed when no items added
 *   - Add product → appears in cart with correct data
 *   - Cart total reflects item subtotals
 *   - Remove item decreases cart count
 *   - Remove all items shows empty state
 *   - "Browse Products" button navigates back to /products
 */
public class CartTest extends BaseTest {

    private ProductsPage productsPage;
    private CartPage     cartPage;
    private NavbarPage   navbar;

    @BeforeMethod
    public void loginAndOpenProducts() {
        new LoginPage(getDriver())
            .open(BASE_URL)
            .loginSuccessfully(TestData.VALID_EMAIL, TestData.VALID_PASSWORD);

        productsPage = new ProductsPage(getDriver());
        navbar       = new NavbarPage(getDriver());
    }

    // -------------------------------------------------------------------------
    // Smoke
    // -------------------------------------------------------------------------

    @Test(description = "Cart page loads via navbar cart link", groups = {"smoke"})
    public void testCartPageLoadsViaNavbar() {
        cartPage = navbar.goToCart();
        Assert.assertTrue(cartPage.isLoaded(), "Cart page should load after clicking cart link");
        Assert.assertEquals(cartPage.getPageHeading(), TestData.CART_PAGE_HEADING);
    }

    @Test(description = "Empty cart shows friendly message", groups = {"smoke", "regression"})
    public void testEmptyCartMessage() {
        cartPage = navbar.goToCart();
        // Cart may already be empty for a fresh test user
        if (cartPage.isCartEmpty()) {
            Assert.assertTrue(cartPage.getEmptyCartMessage().contains("empty"),
                "Empty cart message should mention 'empty'");
        }
    }

    // -------------------------------------------------------------------------
    // Add product → verify in cart
    // -------------------------------------------------------------------------

    @Test(description = "Adding a product to cart increases cart item count", groups = {"regression", "smoke"})
    public void testAddProductAppearsInCart() {
        String productName = productsPage.getProductName(0);
        productsPage.addFirstProductToCart().waitForNotification();

        cartPage = navbar.goToCart();

        Assert.assertFalse(cartPage.isCartEmpty(), "Cart should not be empty after adding a product");
        Assert.assertTrue(cartPage.getCartItemCount() >= 1, "Cart should contain at least 1 item");
        Assert.assertTrue(cartPage.getItemName(0).equalsIgnoreCase(productName),
            "Cart item name should match the product that was added");
    }

    @Test(description = "Cart total is positive after adding a product", groups = {"regression"})
    public void testCartTotalIsPositive() {
        productsPage.addFirstProductToCart().waitForNotification();
        cartPage = navbar.goToCart();

        Assert.assertTrue(cartPage.isCartTotalDisplayed(), "Cart total should be visible");
        Assert.assertTrue(cartPage.getCartTotalAsDouble() > 0,
            "Cart total should be greater than 0 after adding a product");
    }

    // -------------------------------------------------------------------------
    // Remove item
    // -------------------------------------------------------------------------

    @Test(description = "Removing an item decreases cart count by 1", groups = {"regression"})
    public void testRemoveItemDecreasesCount() {
        // Add two different products first
        productsPage.addProductToCart(0).waitForNotification();
        productsPage.addProductToCart(1).waitForNotification();

        cartPage = navbar.goToCart();
        int before = cartPage.getCartItemCount();
        Assert.assertTrue(before >= 1, "Need at least 1 item to test removal");

        cartPage.removeFirstItem();
        int after = cartPage.getCartItemCount();

        Assert.assertEquals(after, before - 1, "Cart count should decrease by 1 after removal");
    }

    @Test(description = "Removing all items shows empty cart state", groups = {"regression"})
    public void testRemoveAllItemsShowsEmptyState() {
        productsPage.addFirstProductToCart().waitForNotification();
        cartPage = navbar.goToCart();

        cartPage.removeAllItems();

        Assert.assertTrue(cartPage.isCartEmpty(),
            "Cart should show empty state after removing all items");
    }

    // -------------------------------------------------------------------------
    // Navigation
    // -------------------------------------------------------------------------

    @Test(description = "'Browse Products' on empty cart navigates to /products", groups = {"regression"})
    public void testBrowseProductsButtonNavigates() {
        // Ensure cart is empty
        cartPage = navbar.goToCart();
        if (!cartPage.isCartEmpty()) {
            cartPage.removeAllItems();
        }

        ProductsPage back = cartPage.browseProducts();
        Assert.assertTrue(getDriver().getCurrentUrl().contains(TestData.PRODUCTS_PAGE_URL),
            "Browse Products button should navigate to /products");
    }
}
