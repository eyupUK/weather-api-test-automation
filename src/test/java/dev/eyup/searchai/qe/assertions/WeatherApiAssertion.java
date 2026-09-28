package dev.eyup.searchai.qe.assertions;

import io.restassured.http.ContentType;
import io.restassured.response.Response;

import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.Matchers.*;


public class WeatherApiAssertion {

    public static void assertSuccessfulResponseOfCurrentWeather(Response response) {

        response.then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("location.name", not(emptyOrNullString()))
                .body("current.temp_c", instanceOf(Number.class))
                .body("current.humidity", allOf(
                        greaterThanOrEqualTo(0), lessThanOrEqualTo(100)))
                .body("current.condition.text", not(emptyOrNullString()));
    }

    public static void assertMissingApiKeyResponse(Response response) {

        response.then()
                .statusCode(401)
                .contentType(ContentType.JSON)
                .body("error.message", equalTo("API key is invalid or not provided."))
                .body("error.code", equalTo(1002))
                .body(matchesJsonSchemaInClasspath("schemas/rest/current_weather_error_schema.json"));
        ;
    }

    public static void assertInvalidApiKeyResponse(Response response) {

        response.then()
                .statusCode(401)
                .contentType(ContentType.JSON)
                .body("error.message", equalTo("API key is invalid."))
                .body("error.code", equalTo(2006))
                .body(matchesJsonSchemaInClasspath("schemas/rest/current_weather_error_schema.json"));
        ;
    }

    public static void assertMissingQueryResponse(Response response) {

        response.then()
                .statusCode(400)
                .contentType(ContentType.JSON)
                .body("error.message", equalTo("Parameter q is missing."))
                .body("error.code", equalTo(1003))
                .body(matchesJsonSchemaInClasspath("schemas/rest/current_weather_error_schema.json"));
    }
}
