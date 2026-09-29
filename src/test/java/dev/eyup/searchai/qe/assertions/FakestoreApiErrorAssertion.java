package dev.eyup.searchai.qe.assertions;

import io.restassured.response.Response;

import static org.hamcrest.Matchers.*;

public class FakestoreApiErrorAssertion {

    public static void assertServiceUnavailableError(Response response) {
        response.then()
                .log().ifValidationFails()
                .statusCode(503)
                .contentType("application/json")
                .header("Retry-After", equalTo("120"))
                .body("error.code", equalTo("SERVICE_UNAVAILABLE"))
                .body("error.message",
                        equalTo("Product service is temporarily unavailable"));
    }
}
