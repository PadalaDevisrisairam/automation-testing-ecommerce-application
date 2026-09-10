package com.ecommerce.automation.api.tests;

import com.ecommerce.automation.api.base.BaseApiTest;
import com.ecommerce.automation.utils.ConfigReader;
import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;
import io.restassured.response.Response;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

/**
 * CartApiTest — REST Assured tests for the /cart endpoints.
 *
 * Endpoints covered:
 *  POST   /cart                    — add item to cart (→ 201)
 *  GET    /cart/{user_id}          — retrieve cart for user
 *  DELETE /cart/{user_id}/{product_id} — remove item from cart
 *
 * Setup:
 *  - A real user_id is obtained by logging in once via POST /auth/login before any tests run.
 *  - A real product_id is fetched from GET /products before any tests run.
 *  - All tests operate on that resolved user_id / product_id so the suite
 *    remains self-contained (no hard-coded IDs).
 */
@Feature("Cart API")
public class CartApiTest extends BaseApiTest {

    private static final String CART_PATH    = "/cart";
    private static final String AUTH_PATH    = "/auth/login";
    private static final String PRODUCTS_PATH = "/products";

    private int userId;
    private int productId;

    // -----------------------------------------------------------------------
    // One-time setup — resolve user_id and product_id from live API
    // -----------------------------------------------------------------------

    @BeforeClass(alwaysRun = true)
    @Override
    public void setUpApi() {
        // initialise base URI, requestSpec, responseSpec
        super.setUpApi();

        // Authenticate and capture user_id
        Response loginResponse = given()
                .spec(requestSpec)
                .body(String.format("{\"email\":\"%s\",\"password\":\"%s\"}",
                        ConfigReader.getValidEmail(),
                        ConfigReader.getValidPassword()))
                .when()
                .post(AUTH_PATH)
                .then()
                .statusCode(200)
                .extract().response();

        userId = loginResponse.jsonPath().getInt("user_id");

        // Grab the first product's id
        Response productsResponse = given()
                .spec(requestSpec)
                .when()
                .get(PRODUCTS_PATH)
                .then()
                .statusCode(200)
                .extract().response();

        productId = productsResponse.jsonPath().getInt("[0].id");
    }

    // -----------------------------------------------------------------------
    // GET /cart/{user_id}
    // -----------------------------------------------------------------------

    @Test(description = "GET /cart/{user_id} returns 200 with items and total fields",
          groups = {"api", "smoke"}, priority = 1)
    @Story("Get Cart")
    @Severity(SeverityLevel.BLOCKER)
    @Description("GET /cart/{user_id} should return 200 with a JSON body containing " +
                 "'items' (array) and 'total' (number) fields.")
    public void getCart_returns200WithExpectedShape() {
        given()
            .spec(requestSpec)
        .when()
            .get(CART_PATH + "/" + userId)
        .then()
            .spec(responseSpec)
            .statusCode(200)
            .body("items", notNullValue())
            .body("total", notNullValue());
    }

    @Test(description = "GET /cart for unknown user returns 404",
          groups = {"api", "regression"}, priority = 1)
    @Story("Get Cart")
    @Severity(SeverityLevel.NORMAL)
    @Description("Requesting a cart for a user_id that does not exist should return 404.")
    public void getCart_unknownUser_returns404() {
        given()
            .spec(requestSpec)
        .when()
            .get(CART_PATH + "/999999")
        .then()
            .statusCode(404)
            .body("detail", notNullValue());
    }

    // -----------------------------------------------------------------------
    // POST /cart — add item
    // -----------------------------------------------------------------------

    @Test(description = "POST /cart with valid payload returns 201 and updated cart",
          groups = {"api", "smoke"}, priority = 2)
    @Story("Add to Cart")
    @Severity(SeverityLevel.BLOCKER)
    @Description("POST /cart should add the requested product to the user's cart and return 201 " +
                 "with the full updated cart (items array + total).")
    public void addToCart_validPayload_returns201() {
        given()
            .spec(requestSpec)
            .body(addToCartBody(userId, productId, 1))
        .when()
            .post(CART_PATH)
        .then()
            .spec(responseSpec)
            .statusCode(201)
            .body("items",       not(empty()))
            .body("total",       greaterThan(0f))
            .body("items.product_id", hasItem(productId));
    }

    @Test(description = "POST /cart increments quantity when same product added twice",
          groups = {"api", "regression"}, priority = 3)
    @Story("Add to Cart")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Adding the same product_id twice should increment its quantity rather than " +
                 "creating a duplicate cart line.")
    public void addToCart_duplicateProduct_incrementsQuantity() {
        // Add once (may already be in cart from previous test — that's fine, we note the quantity)
        int quantityBefore = given()
                .spec(requestSpec)
                .body(addToCartBody(userId, productId, 1))
                .when()
                .post(CART_PATH)
                .then()
                .statusCode(201)
                .extract()
                .jsonPath()
                .getInt("items.find { it.product_id == " + productId + " }.quantity");

        // Add again
        int quantityAfter = given()
                .spec(requestSpec)
                .body(addToCartBody(userId, productId, 1))
                .when()
                .post(CART_PATH)
                .then()
                .statusCode(201)
                .extract()
                .jsonPath()
                .getInt("items.find { it.product_id == " + productId + " }.quantity");

        org.testng.Assert.assertEquals(quantityAfter, quantityBefore + 1,
                "Quantity should increment by 1 when the same product is added again");
    }

    @Test(description = "POST /cart with unknown user_id returns 404",
          groups = {"api", "regression"}, priority = 2)
    @Story("Add to Cart")
    @Severity(SeverityLevel.NORMAL)
    @Description("Adding a product for a non-existent user should return 404 with a detail message.")
    public void addToCart_unknownUser_returns404() {
        given()
            .spec(requestSpec)
            .body(addToCartBody(999999, productId, 1))
        .when()
            .post(CART_PATH)
        .then()
            .statusCode(404)
            .body("detail", containsStringIgnoringCase("user"));
    }

    @Test(description = "POST /cart with unknown product_id returns 404",
          groups = {"api", "regression"}, priority = 2)
    @Story("Add to Cart")
    @Severity(SeverityLevel.NORMAL)
    @Description("Adding a product that does not exist should return 404 with a detail message.")
    public void addToCart_unknownProduct_returns404() {
        given()
            .spec(requestSpec)
            .body(addToCartBody(userId, 999999, 1))
        .when()
            .post(CART_PATH)
        .then()
            .statusCode(404)
            .body("detail", containsStringIgnoringCase("product"));
    }

    @Test(description = "POST /cart missing required fields returns 422",
          groups = {"api", "regression"}, priority = 2)
    @Story("Add to Cart")
    @Severity(SeverityLevel.MINOR)
    @Description("An incomplete payload missing user_id or product_id should return 422.")
    public void addToCart_missingFields_returns422() {
        given()
            .spec(requestSpec)
            .body("{}")
        .when()
            .post(CART_PATH)
        .then()
            .statusCode(422);
    }

    // -----------------------------------------------------------------------
    // Cart item shape after add
    // -----------------------------------------------------------------------

    @Test(description = "Cart item object contains product_id, name, price, quantity, subtotal",
          groups = {"api", "regression"}, priority = 3)
    @Story("Add to Cart")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Each item in the cart response must expose product_id, name, price, quantity, " +
                 "and subtotal so the frontend cart page can render correctly.")
    public void cartItem_hasRequiredFields() {
        given()
            .spec(requestSpec)
            .body(addToCartBody(userId, productId, 1))
        .when()
            .post(CART_PATH)
        .then()
            .statusCode(201)
            .body("items[0].product_id", notNullValue())
            .body("items[0].name",       notNullValue())
            .body("items[0].price",      greaterThan(0f))
            .body("items[0].quantity",   greaterThan(0))
            .body("items[0].subtotal",   greaterThan(0f));
    }

    @Test(description = "Cart subtotal equals price × quantity for each item",
          groups = {"api", "regression"}, priority = 3)
    @Story("Add to Cart")
    @Severity(SeverityLevel.NORMAL)
    @Description("The subtotal for every cart item must equal price * quantity (rounded to 2 d.p.).")
    public void cartItem_subtotalEqualsPrice_x_Quantity() {
        io.restassured.path.json.JsonPath json = given()
                .spec(requestSpec)
                .body(addToCartBody(userId, productId, 1))
                .when()
                .post(CART_PATH)
                .then()
                .statusCode(201)
                .extract().jsonPath();

        java.util.List<java.util.Map<String, Object>> items = json.getList("items");
        for (java.util.Map<String, Object> item : items) {
            float price    = ((Number) item.get("price")).floatValue();
            int   quantity = ((Number) item.get("quantity")).intValue();
            float subtotal = ((Number) item.get("subtotal")).floatValue();
            float expected = Math.round(price * quantity * 100f) / 100f;

            org.testng.Assert.assertEquals(subtotal, expected, 0.01f,
                    "Subtotal mismatch for product_id " + item.get("product_id"));
        }
    }

    // -----------------------------------------------------------------------
    // DELETE /cart/{user_id}/{product_id}
    // -----------------------------------------------------------------------

    @Test(description = "DELETE /cart/{user_id}/{product_id} removes item and returns updated cart",
          groups = {"api", "regression"}, priority = 10)
    @Story("Remove from Cart")
    @Severity(SeverityLevel.CRITICAL)
    @Description("DELETE /cart/{user_id}/{product_id} should remove the product from the cart " +
                 "and return 200 with the updated cart (the product_id must no longer appear in items).")
    public void removeFromCart_existingItem_returns200() {
        // Ensure the item is in the cart first
        given()
            .spec(requestSpec)
            .body(addToCartBody(userId, productId, 1))
        .when()
            .post(CART_PATH)
        .then()
            .statusCode(201);

        // Now remove it
        given()
            .spec(requestSpec)
        .when()
            .delete(CART_PATH + "/" + userId + "/" + productId)
        .then()
            .spec(responseSpec)
            .statusCode(200)
            .body("items.product_id", not(hasItem(productId)));
    }

    @Test(description = "DELETE /cart for non-existent item returns 404",
          groups = {"api", "regression"}, priority = 10)
    @Story("Remove from Cart")
    @Severity(SeverityLevel.NORMAL)
    @Description("Attempting to delete a product that is not in the cart should return 404.")
    public void removeFromCart_nonExistentItem_returns404() {
        given()
            .spec(requestSpec)
        .when()
            .delete(CART_PATH + "/" + userId + "/999999")
        .then()
            .statusCode(404)
            .body("detail", notNullValue());
    }

    // -----------------------------------------------------------------------
    // Helper
    // -----------------------------------------------------------------------

    private String addToCartBody(int uId, int pId, int qty) {
        return String.format("{\"user_id\":%d,\"product_id\":%d,\"quantity\":%d}", uId, pId, qty);
    }
}
