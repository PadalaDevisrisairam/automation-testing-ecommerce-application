package com.ecommerce.automation.cucumber.steps;

import com.ecommerce.automation.pages.CartPage;
import com.ecommerce.automation.pages.NavbarPage;
import com.ecommerce.automation.pages.ProductsPage;
import com.ecommerce.automation.utils.ConfigReader;
import io.cucumber.java.en.*;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Step definitions for cart.feature.
 */
public class CartSteps {

    private final ScenarioContext ctx;
    private CartPage     cartPage;
    private NavbarPage   navbar;

    public CartSteps(ScenarioContext ctx) {
        this.ctx = ctx;
    }

    // ----- Given ------------------------------------------------------------

    @Given("the user has an empty cart")
    public void userHasEmptyCart() {
        // Navigate to cart; remove all items if any exist
        navbar   = new NavbarPage(ctx.getDriver());
        cartPage = navbar.goToCart();
        if (!cartPage.isCartEmpty()) {
            cartPage.removeAllItems();
        }
        // Return to products page (other steps may need it)
        ctx.getDriver().get(ConfigReader.getBaseUrl() + "/products");
    }

    @Given("the user has added {int} different products to the cart")
    public void userHasAddedNProducts(int n) {
        ProductsPage products = new ProductsPage(ctx.getDriver());
        for (int i = 0; i < n; i++) {
            products.addProductToCart(i).waitForNotification();
            // Brief pause between adds so the notification doesn't overlap
            try { Thread.sleep(300); } catch (InterruptedException ignored) {}
        }
    }

    // ----- When -------------------------------------------------------------

    @When("the user clicks the cart link in the navbar")
    public void clickCartLink() {
        navbar   = new NavbarPage(ctx.getDriver());
        cartPage = navbar.goToCart();
    }

    @When("the user removes the first cart item")
    public void removeFirstCartItem() {
        ctx.setCartItemCountBefore(cartPage.getCartItemCount());
        cartPage.removeFirstItem();
    }

    @When("the user removes all cart items")
    public void removeAllCartItems() {
        cartPage.removeAllItems();
    }

    @When("the user clicks the {string} button")
    public void clickButtonWithLabel(String label) {
        if ("Browse Products".equalsIgnoreCase(label)) {
            cartPage.browseProducts();
        }
    }

    // ----- Then -------------------------------------------------------------

    @Then("the cart page should be loaded")
    public void cartPageLoaded() {
        assertThat(cartPage.isLoaded()).isTrue();
    }

    @Then("the page heading should be {string}")
    public void pageHeadingIs(String expected) {
        assertThat(cartPage.getPageHeading()).isEqualTo(expected);
    }

    @Then("the empty cart message should be visible")
    public void emptyCartMessageVisible() {
        assertThat(cartPage.isCartEmpty()).isTrue();
        assertThat(cartPage.getEmptyCartMessage()).containsIgnoringCase("empty");
    }

    @Then("the cart should contain at least {int} item")
    public void cartContainsAtLeastNItems(int min) {
        assertThat(cartPage.getCartItemCount()).isGreaterThanOrEqualTo(min);
    }

    @Then("the first cart item name should match the added product")
    public void firstItemNameMatchesAddedProduct() {
        String noted = ctx.getNotedProductName();
        assertThat(noted).as("No product name was noted — check products steps").isNotNull();
        assertThat(cartPage.getItemName(0)).isEqualToIgnoringCase(noted);
    }

    @Then("the cart total should be greater than {int}")
    public void cartTotalGreaterThan(int min) {
        assertThat(cartPage.getCartTotalAsDouble()).isGreaterThan(min);
    }

    @Then("the cart item count should decrease by {int}")
    public void cartItemCountDecreasedBy(int delta) {
        int before = ctx.getCartItemCountBefore();
        int after  = cartPage.getCartItemCount();
        assertThat(after).isEqualTo(before - delta);
    }
}
