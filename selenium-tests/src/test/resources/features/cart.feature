# ============================================================
#  Feature: Shopping Cart
#  Covers Cart.jsx — adding, viewing, removing items, navigation.
# ============================================================
@cart
Feature: Shopping Cart

  Background:
    Given the user is logged in with valid credentials
    And the user is on the products page

  # ------------------------------------------------------------------
  # Smoke
  # ------------------------------------------------------------------

  @smoke
  Scenario: Cart page loads via the navbar cart link
    When the user clicks the cart link in the navbar
    Then the cart page should be loaded
    And the page heading should be "Shopping Cart"

  @smoke @regression
  Scenario: Empty cart displays a friendly message
    Given the user has an empty cart
    When the user clicks the cart link in the navbar
    Then the empty cart message should be visible

  # ------------------------------------------------------------------
  # Add product → verify in cart
  # ------------------------------------------------------------------

  @smoke @regression
  Scenario: Added product appears in the cart
    When the user adds the first product to the cart
    And the user clicks the cart link in the navbar
    Then the cart should contain at least 1 item
    And the first cart item name should match the added product

  @regression
  Scenario: Cart total is positive after adding a product
    When the user adds the first product to the cart
    And the user clicks the cart link in the navbar
    Then the cart total should be greater than 0

  # ------------------------------------------------------------------
  # Remove item
  # ------------------------------------------------------------------

  @regression
  Scenario: Removing an item decreases the cart item count
    Given the user has added 2 different products to the cart
    When the user clicks the cart link in the navbar
    And the user removes the first cart item
    Then the cart item count should decrease by 1

  @regression
  Scenario: Removing all items shows the empty cart state
    When the user adds the first product to the cart
    And the user clicks the cart link in the navbar
    And the user removes all cart items
    Then the empty cart message should be visible

  # ------------------------------------------------------------------
  # Navigation
  # ------------------------------------------------------------------

  @regression
  Scenario: "Browse Products" button on empty cart navigates to products
    Given the user has an empty cart
    When the user clicks the cart link in the navbar
    And the user clicks the "Browse Products" button
    Then the current URL should contain "/products"
