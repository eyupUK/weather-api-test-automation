package dev.eyup.searchai.qe.tests.api;

import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;


import static dev.eyup.searchai.qe.assertions.WeatherApiAssertion.*;
import static dev.eyup.searchai.qe.config.RequestSpec.baseSpecs;
import static dev.eyup.searchai.qe.config.RequestSpec.baseSpecsWithApiKey;
import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;


@Tag("live")
class CurrentWeatherTest {

    @Test
    @DisplayName("Current weather matches Birmingham coordinates and has valid weather fields")
    void shouldReturnCurrentWeatherForBirminghamCoordinates() {
        RequestSpecification request = given().spec(baseSpecsWithApiKey())
            .queryParam("q", "52.4862,-1.8904");

        Response response = request.when().get("/current.json");

        assertSuccessfulResponseOfCurrentWeather(response);

        response.then().assertThat()
                .body("location.country", equalTo("United Kingdom"))
                .body("location.tz_id", equalTo("Europe/London"))
                .body(matchesJsonSchemaInClasspath("schemas/rest/current_weather_successful.json"));

        // A coordinate lookup can return a nearby locality rather than the city label.
        // This exercise permits 0.05 degrees per axis; it is not a provider SLA.
        assertEquals(52.4862, response.jsonPath().getDouble("location.lat"), 0.05,
            "Returned latitude should be near the requested Birmingham coordinates.");
        assertEquals(-1.8904, response.jsonPath().getDouble("location.lon"), 0.05,
            "Returned longitude should be near the requested Birmingham coordinates.");

    }

    @Test
    @DisplayName("Current weather returns unauthorized response when API key is missing")
    void shouldReturnUnauthorizedResponseWhenApiKeyIsMissing() {
        RequestSpecification request = given().spec(baseSpecs())
                .queryParam("q", "52.4862,-1.8904");

        Response response = request.when().get("/current.json");

        assertMissingApiKeyResponse(response);
    }

    @Test
    @DisplayName("Current weather returns unauthorized response when API key is invalid")
    void shouldReturnUnauthorizedResponseWhenApiKeyIsInvalid() {
        RequestSpecification request = given().spec(baseSpecs())
                .queryParam("key", "dummykey")
                .queryParam("q", "52.4862,-1.8904");

        Response response = request.when().get("/current.json");

        assertInvalidApiKeyResponse(response);
    }

    @Test
    @DisplayName("Current weather returns bad request response when query is missing")
    void shouldReturnBadRequestWhenQueryIsMissing() {

        Response response =
                given()
                        .spec(baseSpecsWithApiKey())
                .when()
                        .get("/current.json");

        assertMissingQueryResponse(response);
    }
}
