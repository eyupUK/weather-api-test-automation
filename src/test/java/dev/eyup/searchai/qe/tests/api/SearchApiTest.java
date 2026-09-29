package dev.eyup.searchai.qe.tests.api;

import dev.eyup.searchai.qe.client.FakestoreApiClient;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;

public class SearchApiTest {

    @Test
    @DisplayName("Search for products by title")
    void searchProductsByTitle() {
        RequestSpecification specs = given()
                .queryParam("q", "java")
                .queryParam("type", "book")
                .queryParam("page", 1)
                .queryParam("size", 2)
                .queryParam("sort", "id,asc");
        FakestoreApiClient client = new FakestoreApiClient(specs);
    }
}
