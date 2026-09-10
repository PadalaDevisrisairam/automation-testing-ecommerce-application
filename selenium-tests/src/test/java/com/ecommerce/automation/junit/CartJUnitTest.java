package com.ecommerce.automation.junit;

import com.ecommerce.automation.base.BaseJUnitTest;
import com.ecommerce.automation.base.FailureScreenshotExtension;
import com.ecommerce.automation.pages.CartPage;
import com.ecommerce.automation.pages.LoginPage;
import com.ecommerce.automation.pages.NavbarPage;
import com.ecommerce.automation.pages.ProductsPage;
import com.ecommerce.automation.utils.TestData;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * JUnit 5 tests for the Cart page.
 */
@ExtendWith(FailureScreenshotExtension.class)
@TestMethodOrder(MethodOrderer.DisplayName.class)
@DisplayName("Cart Page — JUnit 5")
class CartJUnitTest extends BaseJUnitTest {

    private ProductsPage productsPage;
    private NavbarPage   navbar;

    @BeforeEach
    void loginAndOpenProducts(TestInfo info) {
        new LoginPage(driver)
            .open(BASE_URL)
            .loginSuccessfully(TestData.VALID_EMAIL, TestData.VALID_PASSWORD);
        productsPage = new ProductsPage(driver);
        navbar       = new NavbarPage(driver);
    }

    // -------------------------------------------------------------------------
    // Smoke
    // -------------------------------------------------------------------------

    @Test
    @Tag("smoke")
    @DisplayName("Cart page loads via navbar cart link")
    void cartPageLoads() {
        CartPage cart = navbar.goToCart();
        assertThat(cart.isLoaded()).isTrue();
        assertThat(cart.getPageHeading()).isEqualTo(TestData.CART_PAGE_HEADING);
    }

    @Test
    @Tag("smoke")
    @Tag("regression")
    @DisplayName("Empty cart shows friendly empty message")
    void emptyCartMessage() {
        CartPage cart = navbar.goToCart();
        if (cart.isCartEmpty()) {
            assertThat(cart.getEmptyCartMessage()).containsIgnoringCase("empty");
        }
    }

    // -------------------------------------------------------------------------
    // Add product → verify in cart
    // -------------------------------------------------------------------------

    @Test
    @Tag("smoke")
    @Tag("regression")
    @DisplayName("Added product appears in cart with correct name")
    void addedProductAppearsInCart() {
        String name = productsPage.getProductName(0);
        productsPage.addFirstProductToCart().waitForNotification();

        CartPage cart = navbar.goToCart();
        assertThat(cart.isCartEmpty()).isFalse();
        assertThat(cart.getCartItemCount()).isGreaterThanOrEqualTo(1);
        assertThat(cart.getItemName(0)).isEqualToIgnoringCase(name);
    }

    @Test
    @Tag("regression")
    @DisplayName("Cart total is positive after adding a product")
    void cartTotalPositive() {
        productsPage.addFirstProductToCart().waitForNotification();
        CartPage cart = navbar.goToCart();

        assertThat(cart.isCartTotalDisplayed()).isTrue();
        assertThat(cart.getCartTotalAsDouble()).isGreaterThan(0.0);
    }

    // -------------------------------------------------------------------------
    // Remove item
    // -------------------------------------------------------------------------

    @Test
    @Tag("regression")
    @DisplayName("Removing an item decreases cart count by 1")
    void removeItemDecreasesCount() {
        productsPage.addProductToCart(0).waitForNotification();
        productsPage.addProductToCart(1).waitForNotification();

        CartPage cart = navbar.goToCart();
        int before = cart.getCartItemCount();
        assertThat(before).isGreaterThanOrEqualTo(1);

        cart.removeFirstItem();
        assertThat(cart.getCartItemCount()).isEqualTo(before - 1);
    }

    @Test
    @Tag("regression")
    @DisplayName("Removing all items shows empty cart state")
    void removeAllShowsEmptyState() {
        productsPage.addFirstProductToCart().waitForNotification();
        CartPage cart = navbar.goToCart();
        cart.removeAllItems();

        assertThat(cart.isCartEmpty()).isTrue();
    }

    // -------------------------------------------------------------------------
    // Navigation
    // -------------------------------------------------------------------------

    @Test
    @Tag("regression")
    @DisplayName("'Browse Products' on empty cart navigates back to /products")
    void browseProductsNavigates() {
        CartPage cart = navbar.goToCart();
        if (!cart.isCartEmpty()) {
            cart.removeAllItems();
        }
        cart.browseProducts();
        assertThat(driver.getCurrentUrl()).contains(TestData.PRODUCTS_PAGE_URL);
    }
}
