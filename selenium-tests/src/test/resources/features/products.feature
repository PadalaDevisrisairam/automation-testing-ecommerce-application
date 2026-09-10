# ============================================================
#  Feature: Product Listing & Search
#  Covers the Products.jsx page — listing, search, add-to-cart.
# ============================================================
@products
Feature: Product Listing and Search

  Background:
    Given the user is logged in with valid credentials
    And the user is on the products page

  # ------------------------------------------------------------------
  # Smoke
  # ------------------------------------------------------------------

  @smoke
  Scenario: Products page displays a list of products
    Then the products grid should be visible
    And at least 1 product should be listed

  @smoke
  Scenario: URL contains /products after login
    Then the current URL should contain "/products"

  # ------------------------------------------------------------------
  # Search — positive
  # ------------------------------------------------------------------

  @regression
  Scenario: Searching by a valid term filters the product list
    When the user searches for "a"
    Then at least 1 product should be listed
    And all visible product names should contain "a"

  @regression
  Scenario: Clearing the search field restores the full product list
    Given the user has searched for "a"
    When the user clears the search field
    Then the products grid should be visible
    And at least 1 product should be listed

  # ------------------------------------------------------------------
  # Search — negative
  # ------------------------------------------------------------------

  @regression
  Scenario: Searching for a non-existent product shows the empty state
    When the user searches for "zzzxxx999notaproduct"
    Then no products should be listed
    And the empty state message should be visible

  # ------------------------------------------------------------------
  # Add to cart
  # ------------------------------------------------------------------

  @smoke @regression
  Scenario: Adding the first product to cart shows a success notification
    When the user adds the first product to the cart
    Then a success notification should appear
    And the notification should contain "added to cart"

  @regression
  Scenario: Success notification contains the added product name
    Given the first product name is noted
    When the user adds the first product to the cart
    Then the notification should contain the noted product name
