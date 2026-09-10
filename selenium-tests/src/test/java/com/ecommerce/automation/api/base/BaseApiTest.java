package com.ecommerce.automation.api.base;

import com.ecommerce.automation.utils.ConfigReader;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import org.testng.annotations.BeforeClass;

/**
 * BaseApiTest — parent for all REST Assured TestNG test classes.
 *
 * Responsibilities:
 *  - Sets RestAssured.baseURI from config.properties (api.url)
 *  - Builds a shared RequestSpecification (JSON content-type, Allure filter, logging)
 *  - Builds a shared ResponseSpecification (content-type JSON by default)
 *
 * Sub-classes inherit {@code requestSpec} and {@code responseSpec} and can use them
 * directly in given() calls, or override them if a specific test needs different headers.
 */
public class BaseApiTest {

    protected RequestSpecification  requestSpec;
    protected ResponseSpecification responseSpec;

    @BeforeClass(alwaysRun = true)
    public void setUpApi() {
        RestAssured.baseURI = ConfigReader.getApiUrl();
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();

        requestSpec = new RequestSpecBuilder()
                .setContentType(ContentType.JSON)
                .setAccept(ContentType.JSON)
                .addFilter(new AllureRestAssured())          // attaches request/response to Allure report
                .addFilter(new RequestLoggingFilter())       // logs full request to stdout
                .addFilter(new ResponseLoggingFilter())      // logs full response to stdout
                .build();

        responseSpec = new ResponseSpecBuilder()
                .expectContentType(ContentType.JSON)
                .build();
    }
}
