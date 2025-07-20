package com.example;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import io.restassured.RestAssured;
import io.restassured.response.Response;

import static org.hamcrest.Matchers.equalTo;

public class UrlShortenerStepDefinitions {
    private String fullUrl;
    private String alias;
    private Response response;

    @Given("I have a full URL {string}")
    public void iHaveAFullUrl(String url) {
        this.fullUrl = url;
    }

    @When("I shorten it with custom alias {string}")
    public void iShortenItWithCustomAlias(String alias) {
        this.alias = alias;
        response = RestAssured.given()
                .baseUri("http://localhost:8080")
                .contentType("application/json")
                .body("{\"fullUrl\": \"" + fullUrl + "\", \"customAlias\": \"" + alias + "\"}")
                .post("/shorten");
    }

    @Then("I should get a shortened URL {string}")
    public void iShouldGetAShortenedUrl(String shortUrl) {
        response.then().statusCode(201).body("shortUrl", equalTo(shortUrl));
    }

    @Then("the shortened URL should redirect to {string}")
    public void theShortenedUrlShouldRedirectTo(String expectedUrl) {
        Response redirectResponse = RestAssured.given()
                .baseUri("http://localhost:8080")
                .redirects().follow(false)
                .get("/" + alias);
        redirectResponse.then().statusCode(301).header("Location", equalTo(expectedUrl));
    }

    @Given("a shortened URL exists with alias {string}")
    public void aShortenedUrlExistsWithAlias(String alias) {
        this.alias = alias;
        RestAssured.given()
                .baseUri("http://localhost:8080")
                .contentType("application/json")
                .body("{\"fullUrl\": \"https://example.com\", \"customAlias\": \"" + alias + "\"}")
                .post("/shorten");
    }

    @When("I delete the shortened URL")
    public void iDeleteTheShortenedUrl() {
        response = RestAssured.given()
                .baseUri("http://localhost:8080")
                .delete("/" + alias);
    }

    @Then("the shortened URL should no longer exist")
    public void theShortenedUrlShouldNoLongerExist() {
        response.then().statusCode(204);
        RestAssured.given()
                .baseUri("http://localhost:8080")
                .get("/" + alias)
                .then()
                .statusCode(404);
    }
}
