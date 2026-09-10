package com.ecommerce.automation.cucumber.steps;

import com.ecommerce.automation.pages.ProductsPage;
import com.ecommerce.automation.utils.ConfigReader;
import io.cucumber.java.en.*;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Step definitions for products.feature.
 */
public class ProductsSteps {

    private final ScenarioContext ctx;
    private ProductsPage productsPage;

    public ProductsSteps(ScenarioContext ctx) {
        this.ctx = ctx;
    }

    // ----- Given ------------------------------------------------------------

    @Given("the user is on the products page")
    public void userOnProductsPage() {
        productsPage = new ProductsPage(ctx.getDriver())
            .open(ConfigReader.getBaseUrl());
    }

    @Given("the user has searched for {string}")
    public void userHasSearchedFor(String term) {
        productsPage = new ProductsPage(ctx.getDriver());
        productsPage.searchFor(term);
    }

    @Given("the first product name is noted")
    public void noteFirstProductName() {
        productsPage = new ProductsPage(ctx.getDriver());
        ctx.setNotedProductName(productsPage.getProductName(0));
    }

    // ----- When -------------------------------------------------------------

    @When("the user searches for {string}")
    public void userSearchesFor(String term) {
        productsPage.searchFor(term);
    }

    @When("the user clears the search field")
    public void userClearsSearch() {
        productsPage.clearSearch();
    }

    @When("the user adds the first product to the cart")
    public void addFirstProductToCart() {
        // Re-acquire page object in case context was reset
        if (productsPage == null) {
            productsPage = new ProductsPage(ctx.getDriver());
        }
        // Note the product name before adding (for assertions in cart steps)
        ctx.setNotedProductName(productsPage.getProductName(0));
        productsPage.addFirstProductToCart().waitForNotification();
    }

    // ----- Then -------------------------------------------------------------

    @Then("the products grid should be visible")
    public void productsGridVisible() {
        if (productsPage == null) productsPage = new ProductsPage(ctx.getDriver());
        assertThat(productsPage.isLoaded()).isTrue();
    }

    @Then("at least {int} product should be listed")
    public void atLeastNProductsListed(int min) {
        assertThat(productsPage.getProductCount()).isGreaterThanOrEqualTo(min);
    }

    @Then("no products should be listed")
    public void noProductsListed() {
        assertThat(productsPage.getProductCount()).isZero();
    }

    @Then("the empty state message should be visible")
    public void emptyStateVisible() {
        assertThat(productsPage.isEmptyStateDisplayed()).isTrue();
    }

    @Then("all visible product names should contain {string}")
    public void allProductNamesContain(String term) {
        assertThat(productsPage.getAllProductNames())
            .allSatisfy(el ->
                assertThat(el.getText().toLowerCase()).contains(term.toLowerCase())
            );
    }

    @Then("a success notification should appear")
    public void notificationAppears() {
        assertThat(productsPage.isNotificationDisplayed()).isTrue();
    }

    @Then("the notification should contain {string}")
    public void notificationContains(String text) {
        assertThat(productsPage.getNotificationText().toLowerCase())
            .contains(text.toLowerCase());
    }

    @Then("the notification should contain the noted product name")
    public void notificationContainsNotedName() {
        String noted = ctx.getNotedProductName();
        assertThat(noted).as("No product name was noted before this step").isNotNull();
        assertThat(productsPage.getNotificationText()).contains(noted);
    }

    @Then("the current URL should contain {string}")
    public void urlContains(String fragment) {
        assertThat(ctx.getDriver().getCurrentUrl()).contains(fragment);
    }
}
