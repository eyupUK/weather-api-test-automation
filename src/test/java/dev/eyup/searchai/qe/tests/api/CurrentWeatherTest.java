package dev.eyup.searchai.qe.tests.api;

import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;


import static dev.eyup.searchai.qe.assertions.WeatherApiAssertion.*;
import static dev.eyup.searchai.qe.config.RequestSpec.baseSpecs;
import static dev.eyup.searchai.qe.config.RequestSpec.baseSpecsWithApiKey;
import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;


@Tag("live")
class CurrentWeatherTest {

    @ParameterizedTest(name = "[{index}] {2}: coordinates ({0}, {1}), zone {3}")
    @CsvSource({
            "52.4862,-1.8904, United Kingdom, Europe/London",
            "40.7128,-74.0060, United States of America, America/New_York",
            "35.6895,139.6917, Japan, Asia/Tokyo"
    })
    @DisplayName("Current weather matches coordinates for multiple locations and has valid weather fields")
    void shouldReturnCurrentWeatherForMultipleLocations(double lat, double lon, String country, String tzId) {
        RequestSpecification request = given().spec(baseSpecsWithApiKey())
                .queryParam("q", lat + "," + lon);

        Response response = request.when().get("/current.json");

        assertSuccessfulResponseOfCurrentWeather(response);

        response.then().assertThat()
                .body("location.country", equalTo(country))
                .body("location.tz_id", equalTo(tzId))
                .body(matchesJsonSchemaInClasspath("schemas/rest/current_weather_successful.json"));

        // A coordinate lookup can return a nearby locality rather than the city label.
        // This exercise permits 0.05 degrees per axis; it is not a provider SLA.
        assertEquals(lat, response.jsonPath().getDouble("location.lat"), 0.05,
                "Returned latitude should be near the requested coordinates.");
        assertEquals(lon, response.jsonPath().getDouble("location.lon"), 0.05,
                "Returned longitude should be near the requested coordinates.");
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
