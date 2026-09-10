package com.ecommerce.automation.junit;

import com.ecommerce.automation.base.BaseJUnitTest;
import com.ecommerce.automation.base.FailureScreenshotExtension;
import com.ecommerce.automation.pages.LoginPage;
import com.ecommerce.automation.pages.ProductsPage;
import com.ecommerce.automation.utils.TestData;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * JUnit 5 tests for the Products page.
 */
@ExtendWith(FailureScreenshotExtension.class)
@TestMethodOrder(MethodOrderer.DisplayName.class)
@DisplayName("Products Page — JUnit 5")
class ProductsJUnitTest extends BaseJUnitTest {

    private ProductsPage productsPage;

    @BeforeEach
    void loginAndOpenProducts(TestInfo info) {
        new LoginPage(driver)
            .open(BASE_URL)
            .loginSuccessfully(TestData.VALID_EMAIL, TestData.VALID_PASSWORD);
        productsPage = new ProductsPage(driver);
    }

    // -------------------------------------------------------------------------
    // Smoke
    // -------------------------------------------------------------------------

    @Test
    @Tag("smoke")
    @DisplayName("Products grid is visible after login")
    void gridVisible() {
        assertThat(productsPage.isLoaded())
            .as("Products grid or empty-state should be rendered")
            .isTrue();
    }

    @Test
    @Tag("smoke")
    @Tag("regression")
    @DisplayName("At least one product is listed")
    void atLeastOneProduct() {
        assertThat(productsPage.getProductCount())
            .as("Should have at least %d product(s)", TestData.EXPECTED_MIN_PRODUCTS)
            .isGreaterThanOrEqualTo(TestData.EXPECTED_MIN_PRODUCTS);
    }

    @Test
    @Tag("smoke")
    @DisplayName("URL contains /products after login")
    void urlIsProducts() {
        assertThat(driver.getCurrentUrl()).contains(TestData.PRODUCTS_PAGE_URL);
    }

    // -------------------------------------------------------------------------
    // Search
    // -------------------------------------------------------------------------

    @Test
    @Tag("regression")
    @DisplayName("Search with a valid term filters results")
    void searchFiltersProducts() {
        int allCount = productsPage.getProductCount();
        productsPage.searchFor(TestData.SEARCH_TERM_VALID);

        assertThat(productsPage.anyProductContains(TestData.SEARCH_TERM_VALID))
            .as("Search results should contain products matching '%s'", TestData.SEARCH_TERM_VALID)
            .isTrue();
        assertThat(productsPage.getProductCount()).isLessThanOrEqualTo(allCount);
    }

    @Test
    @Tag("regression")
    @DisplayName("Search with no match shows empty state")
    void searchNoMatchShowsEmptyState() {
        productsPage.searchFor(TestData.SEARCH_TERM_NO_MATCH);

        assertThat(productsPage.isEmptyStateDisplayed()).isTrue();
        assertThat(productsPage.getProductCount()).isZero();
    }

    // @Test
    // @Tag("regression")
    // @DisplayName("Clearing search restores full product list")
    // void clearSearchRestoresList() {
    //     int initialCount = productsPage.getProductCount();
    //     productsPage.searchFor(TestData.SEARCH_TERM_NO_MATCH).clearSearch();

    //     assertThat(productsPage.getProductCount()).isEqualTo(initialCount);
    // }

    // -------------------------------------------------------------------------
    // Add to Cart
    // -------------------------------------------------------------------------

    @Test
    @Tag("smoke")
    @Tag("regression")
    @DisplayName("Add first product shows success notification")
    void addToCartShowsNotification() {
        productsPage.addFirstProductToCart().waitForNotification();

        assertThat(productsPage.isNotificationDisplayed()).isTrue();
        assertThat(productsPage.getNotificationText().toLowerCase()).contains("added to cart");
    }

    @Test
    @Tag("regression")
    @DisplayName("Add to cart notification contains product name")
    void notificationContainsProductName() {
        String name = productsPage.getProductName(0);
        productsPage.addFirstProductToCart().waitForNotification();

        assertThat(productsPage.getNotificationText()).contains(name);
    }
}
