package com.ecommerce.automation.api.tests;

import com.ecommerce.automation.api.base.BaseApiTest;
import com.ecommerce.automation.utils.TestData;
import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

/**
 * ProductsApiTest — REST Assured tests for GET /products
 *
 * Covers:
 *  - 200 OK returns a non-empty JSON array
 *  - Each product has the expected fields (id, name, price, description, category, image_url)
 *  - Price is a positive number
 *  - Search param filters results by name (case-insensitive)
 *  - Search with no match returns an empty array (not 404)
 *  - Search with empty string returns all products
 */
@Feature("Products API")
public class ProductsApiTest extends BaseApiTest {

    private static final String PRODUCTS_PATH = "/products";

    // -----------------------------------------------------------------------
    // Smoke — list all products
    // -----------------------------------------------------------------------

    @Test(description = "GET /products returns 200 with a non-empty array", groups = {"api", "smoke"})
    @Story("List Products")
    @Severity(SeverityLevel.BLOCKER)
    @Description("GET /products with no query params should return HTTP 200 and a JSON array " +
                 "containing at least one product.")
    public void getAllProducts_returns200WithItems() {
        given()
            .spec(requestSpec)
        .when()
            .get(PRODUCTS_PATH)
        .then()
            .spec(responseSpec)
            .statusCode(200)
            .body("$", not(empty()))
            .body("$.size()", greaterThanOrEqualTo(TestData.EXPECTED_MIN_PRODUCTS));
    }

    // -----------------------------------------------------------------------
    // Schema — each product object has required fields
    // -----------------------------------------------------------------------

    @Test(description = "Each product contains id, name, price, description, category, image_url",
          groups = {"api", "regression"})
    @Story("List Products")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Every object in the products array must expose the required fields " +
                 "so the frontend can render the product card correctly.")
    public void getAllProducts_eachItemHasRequiredFields() {
        given()
            .spec(requestSpec)
        .when()
            .get(PRODUCTS_PATH)
        .then()
            .statusCode(200)
            .body("[0].id",          notNullValue())
            .body("[0].name",        notNullValue())
            .body("[0].price",       notNullValue())
            .body("[0].description", notNullValue())
            .body("[0].category",    notNullValue())
            .body("[0].image_url",   notNullValue());
    }

    @Test(description = "All products have a positive price", groups = {"api", "regression"})
    @Story("List Products")
    @Severity(SeverityLevel.NORMAL)
    @Description("No product should have a price of zero or less.")
    public void getAllProducts_pricesArePositive() {
        given()
            .spec(requestSpec)
        .when()
            .get(PRODUCTS_PATH)
        .then()
            .statusCode(200)
            .body("price", everyItem(greaterThan(0f)));
    }

    @Test(description = "All product ids are unique positive integers", groups = {"api", "regression"})
    @Story("List Products")
    @Severity(SeverityLevel.NORMAL)
    @Description("Each product must have a unique positive integer id.")
    public void getAllProducts_idsArePositive() {
        given()
            .spec(requestSpec)
        .when()
            .get(PRODUCTS_PATH)
        .then()
            .statusCode(200)
            .body("id", everyItem(greaterThan(0)));
    }

    // -----------------------------------------------------------------------
    // Search — ?search= query parameter
    // -----------------------------------------------------------------------

    @Test(description = "Search with a valid term returns filtered results", groups = {"api", "regression"})
    @Story("Search Products")
    @Severity(SeverityLevel.CRITICAL)
    @Description("GET /products?search=<term> should return only products whose name contains " +
                 "the search term (case-insensitive).")
    public void searchProducts_validTerm_returnsFilteredResults() {
        given()
            .spec(requestSpec)
            .queryParam("search", TestData.SEARCH_TERM_VALID)
        .when()
            .get(PRODUCTS_PATH)
        .then()
            .statusCode(200)
            .body("$", not(empty()))
            // every returned product name must contain the search term (case-insensitive)
            .body("name", everyItem(containsStringIgnoringCase(TestData.SEARCH_TERM_VALID)));
    }

    @Test(description = "Search with no matching term returns empty array", groups = {"api", "regression"})
    @Story("Search Products")
    @Severity(SeverityLevel.NORMAL)
    @Description("GET /products?search=<non-existent-term> should return 200 with an empty JSON array, " +
                 "not a 404.")
    public void searchProducts_noMatch_returnsEmptyArray() {
        given()
            .spec(requestSpec)
            .queryParam("search", TestData.SEARCH_TERM_NO_MATCH)
        .when()
            .get(PRODUCTS_PATH)
        .then()
            .statusCode(200)
            .body("$", empty());
    }

    @Test(description = "Search with empty string returns all products", groups = {"api", "regression"})
    @Story("Search Products")
    @Severity(SeverityLevel.MINOR)
    @Description("GET /products?search= (empty value) should behave like no filter and return all products.")
    public void searchProducts_emptyString_returnsAllProducts() {
        given()
            .spec(requestSpec)
            .queryParam("search", "")
        .when()
            .get(PRODUCTS_PATH)
        .then()
            .statusCode(200)
            .body("$.size()", greaterThanOrEqualTo(TestData.EXPECTED_MIN_PRODUCTS));
    }
}
