package com.ecommerce.automation.utils;

/**
 * TestData centralises all test constants used across TestNG, JUnit and Cucumber tests.
 * Values that differ by environment should be sourced from ConfigReader instead.
 */
public final class TestData {

    private TestData() {}

    // ----- Credentials -------------------------------------------------------
    public static final String VALID_EMAIL    = ConfigReader.getValidEmail();
    public static final String VALID_PASSWORD = ConfigReader.getValidPassword();

    public static final String INVALID_EMAIL    = ConfigReader.getInvalidEmail();
    public static final String INVALID_PASSWORD = ConfigReader.getInvalidPassword();

    public static final String EMPTY_EMAIL    = "";
    public static final String EMPTY_PASSWORD = "";

    // ----- Expected UI text --------------------------------------------------
    public static final String LOGIN_PAGE_TITLE    = "🛍️ ShopPOC";
    public static final String PRODUCTS_PAGE_URL   = "/products";
    public static final String CART_PAGE_URL        = "/cart";
    public static final String LOGIN_PAGE_URL       = "/login";

    public static final String EMPTY_FIELDS_ERROR  = "Please enter both email and password.";
    public static final String INVALID_CREDS_ERROR = "Invalid email or password.";

    public static final String EMPTY_CART_MESSAGE  = "Your cart is empty.";
    public static final String CART_PAGE_HEADING   = "Shopping Cart";

    // ----- Search terms ------------------------------------------------------
    public static final String SEARCH_TERM_VALID   = "a";      // should match several products
    public static final String SEARCH_TERM_NO_MATCH = "zzzxxx999notaproduct";

    // ----- Misc --------------------------------------------------------------
    public static final int EXPECTED_MIN_PRODUCTS = 1;
}
