# ============================================================
#  Feature: User Login
#  Covers the Login.jsx page — positive, negative, and edge flows.
# ============================================================
@login
Feature: User Login

  Background:
    Given the user navigates to the login page

  # ------------------------------------------------------------------
  # Smoke
  # ------------------------------------------------------------------

  @smoke
  Scenario: Login page displays all required elements
    Then the email input should be visible
    And the password input should be visible
    And the sign-in button should be visible
    And the page title should contain "ShopPOC"

  @smoke
  Scenario: Demo credentials hint is visible
    Then the login hint should contain "test@example.com"
    And the login hint should contain "password123"

  # ------------------------------------------------------------------
  # Positive
  # ------------------------------------------------------------------

  @smoke @regression
  Scenario: Successful login with valid credentials
    When the user enters email "test@example.com" and password "password123"
    And the user clicks the sign-in button
    Then the user should be redirected to the products page

  # ------------------------------------------------------------------
  # Negative
  # ------------------------------------------------------------------

  @regression
  Scenario: Error shown when both fields are empty
    When the user clicks the sign-in button
    Then an error message should be displayed
    And the error message should read "Please enter both email and password."

  @regression
  Scenario: Error shown for wrong password
    When the user enters email "test@example.com" and password "wrongpassword"
    And the user clicks the sign-in button
    Then an error message should be displayed
    And the error message should read "Invalid email or password."

  @regression
  Scenario: Error shown for unknown email
    When the user enters email "nobody@example.com" and password "password123"
    And the user clicks the sign-in button
    Then an error message should be displayed
    And the error message should read "Invalid email or password."

  # ------------------------------------------------------------------
  # Data-driven (Scenario Outline)
  # ------------------------------------------------------------------

  @regression
  Scenario Outline: Invalid credentials always show an error
    When the user enters email "<email>" and password "<password>"
    And the user clicks the sign-in button
    Then an error message should be displayed
    And the error message should read "<expected_error>"

    Examples:
      | email                 | password      | expected_error               |
      | notanemail            | password123   | Invalid email or password.   |
      | wrong@example.com     | short         | Invalid email or password.   |
      | wrong@example.com     | wrongpassword | Invalid email or password.   |

  # ------------------------------------------------------------------
  # Logout
  # ------------------------------------------------------------------

  @regression
  Scenario: Logout returns the user to the login page
    Given the user is logged in with valid credentials
    When the user clicks the logout button
    Then the user should be redirected to the login page
