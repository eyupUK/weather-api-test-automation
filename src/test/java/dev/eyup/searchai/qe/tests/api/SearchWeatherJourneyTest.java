package dev.eyup.searchai.qe.tests.api;

import dev.eyup.searchai.qe.model.request.SearchLocation;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static dev.eyup.searchai.qe.assertions.WeatherApiAssertion.assertSuccessfulResponseOfCurrentWeather;
import static dev.eyup.searchai.qe.config.RequestSpec.baseSpecsWithApiKey;
import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class SearchWeatherJourneyTest {

    @Test
    @DisplayName("Should retrieve weather for a location selected from search")
    void shouldRetrieveWeatherForLocationSelectedFromSearch() {
        // Step 1: Search for a location
        // (This would involve making a search API call and verifying the response)
        Response response = given().spec(baseSpecsWithApiKey())
                .queryParam("q", "52.4862,-1.8904")
                .when()
                .get("/search.json");
        response.then().assertThat()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("$", hasSize(greaterThan(0)))
                .body("$", hasSize(1));

        // Step 2: elect: find the result whose name is Nechells Green and country is United Kingdom. Don’t assume the first result is correct. Fail clearly if there isn’t exactly one match
        response.then().assertThat()
                .body("[0].name", equalTo("Nechells Green"))
                .body("[0].country", equalTo("United Kingdom"));
        // Step 3: Extract: capture its id, lat, and lon.
        int id = response.jsonPath().getInt("[0].id");
        double lat = response.jsonPath().getDouble("[0].lat");
        double lon = response.jsonPath().getDouble("[0].lon");

        // Step 4: Request: use the captured id to request the current weather for that location
        Response weatherResponse = given().spec(baseSpecsWithApiKey())
                .queryParam("q", "id:" + id)
                .when()
                .get("/current.json");

        assertSuccessfulResponseOfCurrentWeather(weatherResponse);
        weatherResponse.then()
                .body("location.country", equalTo("United Kingdom"))
                .body("location.tz_id", equalTo("Europe/London"));
        assertEquals(lat, weatherResponse.jsonPath().getDouble("location.lat"), 0.05, "Latitude should match the selected location's latitude within a reasonable margin.");
        assertEquals(lon, weatherResponse.jsonPath().getDouble("location.lon"), 0.05, "Longitude should match the selected location's longitude within a reasonable margin.");
        weatherResponse.then().body(matchesJsonSchemaInClasspath("schemas/rest/current_weather_successful.json"));
    }

    @Test
    @DisplayName("Should retrieve weather for a location selected from search with POJO deserialization")
    void shouldRetrieveWeatherForLocationSelectedFromSearchPOJO() {
        // Step 1: Search for a location
        // (This would involve making a search API call and verifying the response)
        double requestedLat = 52.4862;
        double requestedLon = -1.8904;
        Response response = given().spec(baseSpecsWithApiKey())
                .queryParam("q", requestedLat + "," + requestedLon)
                .when()
                .get("/search.json");

        // Deserialize the response into SearchLocation[].
        // Select the single object.
        SearchLocation location = (response.as(SearchLocation[].class))[0];

        // Assert that its ID and coordinates are non-null.
        assertNotNull(location.getId(), "Location ID should not be null");
        assertNotNull(location.getLat(), "Location latitude should not be null");
        assertNotNull(location.getLon(), "Location longitude should not be null");

        // Use its getters to validate the country and proximity to the requested coordinates.
        assertEquals("United Kingdom", location.getCountry(), "Location country should be United Kingdom");
        assertEquals(requestedLat, location.getLat(), 0.05, "Location latitude should be near the requested coordinates");
        assertEquals(requestedLon, location.getLon(), 0.05, "Location longitude should be near the requested coordinates");

        // Step 3: Extract: capture its id, lat, and lon.
        int id = location.getId();
        double lat = location.getLat();
        double lon = location.getLon();

        // Step 4: Request: use the captured id to request the current weather for that location
        Response weatherResponse = given().spec(baseSpecsWithApiKey())
                .queryParam("q", "id:" + id)
                .when()
                .get("/current.json");

        assertSuccessfulResponseOfCurrentWeather(weatherResponse);
        weatherResponse.then().assertThat()
                .body("location.country", equalTo("United Kingdom"))
                .body("location.tz_id", equalTo("Europe/London"));
        assertEquals(lat, weatherResponse.jsonPath().getDouble("location.lat"), 0.05, "Latitude should match the selected location's latitude within a reasonable margin.");
        assertEquals(lon, weatherResponse.jsonPath().getDouble("location.lon"), 0.05, "Longitude should match the selected location's longitude within a reasonable margin.");
        weatherResponse.then().body(matchesJsonSchemaInClasspath("schemas/rest/current_weather_successful.json"));
    }
}
