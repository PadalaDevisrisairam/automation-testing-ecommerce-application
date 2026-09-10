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
 * AuthApiTest — REST Assured tests for POST /auth/login
 *
 * Covers:
 *  - 200 OK with valid credentials → correct response body shape
 *  - 401 Unauthorized with wrong password
 *  - 401 Unauthorized with unknown email
 *  - 422 Unprocessable Entity when required fields are missing
 */
@Feature("Auth API")
public class AuthApiTest extends BaseApiTest {

    private static final String LOGIN_PATH = "/auth/login";

    // -----------------------------------------------------------------------
    // Happy path
    // -----------------------------------------------------------------------

    @Test(description = "Valid credentials return 200 with user info", groups = {"api", "smoke"})
    @Story("Login")
    @Severity(SeverityLevel.BLOCKER)
    @Description("POST /auth/login with correct email and password should return 200 and a JSON body " +
                 "containing message, user_id, and email fields.")
    public void loginWithValidCredentials_returns200() {
        given()
            .spec(requestSpec)
            .body(loginBody(TestData.VALID_EMAIL, TestData.VALID_PASSWORD))
        .when()
            .post(LOGIN_PATH)
        .then()
            .spec(responseSpec)
            .statusCode(200)
            .body("message", equalTo("Login successful"))
            .body("user_id", notNullValue())
            .body("user_id", greaterThan(0))
            .body("email",   equalTo(TestData.VALID_EMAIL));
    }

    @Test(description = "Valid credentials return correct email in response", groups = {"api", "smoke"})
    @Story("Login")
    @Severity(SeverityLevel.CRITICAL)
    @Description("The email returned in the response body must match the email used to authenticate.")
    public void loginResponse_emailMatchesInput() {
        given()
            .spec(requestSpec)
            .body(loginBody(TestData.VALID_EMAIL, TestData.VALID_PASSWORD))
        .when()
            .post(LOGIN_PATH)
        .then()
            .statusCode(200)
            .body("email", equalTo(TestData.VALID_EMAIL));
    }

    // -----------------------------------------------------------------------
    // Negative — wrong credentials
    // -----------------------------------------------------------------------

    @Test(description = "Wrong password returns 401", groups = {"api", "regression"})
    @Story("Login")
    @Severity(SeverityLevel.CRITICAL)
    @Description("POST /auth/login with a valid email but incorrect password should return 401 " +
                 "and a detail message indicating invalid credentials.")
    public void loginWithWrongPassword_returns401() {
        given()
            .spec(requestSpec)
            .body(loginBody(TestData.VALID_EMAIL, TestData.INVALID_PASSWORD))
        .when()
            .post(LOGIN_PATH)
        .then()
            .statusCode(401)
            .body("detail", containsStringIgnoringCase("invalid"));
    }

    @Test(description = "Unknown email returns 401", groups = {"api", "regression"})
    @Story("Login")
    @Severity(SeverityLevel.CRITICAL)
    @Description("POST /auth/login with an email that does not exist in the system should return 401.")
    public void loginWithUnknownEmail_returns401() {
        given()
            .spec(requestSpec)
            .body(loginBody(TestData.INVALID_EMAIL, TestData.VALID_PASSWORD))
        .when()
            .post(LOGIN_PATH)
        .then()
            .statusCode(401)
            .body("detail", containsStringIgnoringCase("invalid"));
    }

    @Test(description = "Both wrong credentials return 401", groups = {"api", "regression"})
    @Story("Login")
    @Severity(SeverityLevel.NORMAL)
    @Description("POST /auth/login with both a wrong email and wrong password should return 401.")
    public void loginWithBothWrongCredentials_returns401() {
        given()
            .spec(requestSpec)
            .body(loginBody(TestData.INVALID_EMAIL, TestData.INVALID_PASSWORD))
        .when()
            .post(LOGIN_PATH)
        .then()
            .statusCode(401);
    }

    // -----------------------------------------------------------------------
    // Negative — missing / malformed payload
    // -----------------------------------------------------------------------

    @Test(description = "Missing email field returns 422", groups = {"api", "regression"})
    @Story("Login")
    @Severity(SeverityLevel.NORMAL)
    @Description("POST /auth/login without the email field should return 422 Unprocessable Entity.")
    public void loginMissingEmail_returns422() {
        given()
            .spec(requestSpec)
            .body("{\"password\":\"" + TestData.VALID_PASSWORD + "\"}")
        .when()
            .post(LOGIN_PATH)
        .then()
            .statusCode(422);
    }

    @Test(description = "Missing password field returns 422", groups = {"api", "regression"})
    @Story("Login")
    @Severity(SeverityLevel.NORMAL)
    @Description("POST /auth/login without the password field should return 422 Unprocessable Entity.")
    public void loginMissingPassword_returns422() {
        given()
            .spec(requestSpec)
            .body("{\"email\":\"" + TestData.VALID_EMAIL + "\"}")
        .when()
            .post(LOGIN_PATH)
        .then()
            .statusCode(422);
    }

    @Test(description = "Empty request body returns 422", groups = {"api", "regression"})
    @Story("Login")
    @Severity(SeverityLevel.MINOR)
    @Description("POST /auth/login with an empty JSON body should return 422 Unprocessable Entity.")
    public void loginEmptyBody_returns422() {
        given()
            .spec(requestSpec)
            .body("{}")
        .when()
            .post(LOGIN_PATH)
        .then()
            .statusCode(422);
    }

    // -----------------------------------------------------------------------
    // Helper
    // -----------------------------------------------------------------------

    /** Builds a minimal login JSON body string. */
    private String loginBody(String email, String password) {
        return String.format("{\"email\":\"%s\",\"password\":\"%s\"}", email, password);
    }
}
