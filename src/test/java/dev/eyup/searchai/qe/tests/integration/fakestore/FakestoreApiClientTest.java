package dev.eyup.searchai.qe.tests.integration.fakestore;

import com.github.tomakehurst.wiremock.WireMockServer;
import dev.eyup.searchai.qe.client.FakestoreApiClient;
import dev.eyup.searchai.qe.model.request.CreateProductRequest;
import org.junit.jupiter.api.*;

import java.math.BigDecimal;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static io.restassured.RestAssured.given;
import static io.restassured.config.JsonConfig.jsonConfig;
import static io.restassured.config.RestAssuredConfig.config;
import static io.restassured.path.json.config.JsonPathConfig.NumberReturnType.BIG_DECIMAL;

@Tag("stub")
class FakestoreApiClientTest {
    // WireMock lifecycle, stubs, tests, and request verification

    static WireMockServer wm = new WireMockServer(options().dynamicPort());

    @BeforeAll
    static void setup() {
        wm.start();
        // Configure stubs for Fakestore API endpoints here
    }

    @BeforeEach
    void resetWireMock() {
        wm.resetAll();
    }

    @AfterAll
    static void teardown() {
        wm.stop();
    }

    @Test
    @DisplayName("Create a new product in the FakeStore API using WireMock stubs")
    void createProduct() {
        // Implement test logic for creating a product using the Fakestore API client
        CreateProductRequest product = new CreateProductRequest("Test Product", "A test product", "electronics", "https://example.com/image.jpg", new java.math.BigDecimal("19.99"));
        String body = String.format("{\"title\":\"%s\",\"description\":\"%s\",\"category\":\"%s\",\"image\":\"%s\",\"price\":%s}",
                product.getTitle(), product.getDescription(), product.getCategory(), product.getImage(), product.getPrice());

        // Stub for the Fakestore API endpoint to return a successful response for product creation
         wm.stubFor(post(urlEqualTo("/products"))
                         .withHeader("Content-Type", equalTo("application/json"))
                            .withRequestBody(equalToJson(body))
                 .willReturn(aResponse()
                         .withStatus(201)
                         .withHeader("Content-Type", "application/json")
                         .withBody(body)));

         // Call the Fakestore API client to create the product and verify the response
        FakestoreApiClient client = new FakestoreApiClient(given().baseUri(wm.baseUrl()).contentType("application/json").config((config().jsonConfig(
                jsonConfig().numberReturnType(BIG_DECIMAL)))));

        client.createProduct(product)
                .then().log().ifValidationFails()
                .assertThat()
                .statusCode(201)
                .contentType("application/json")
                .body("title", org.hamcrest.Matchers.equalTo(product.getTitle()))
                .body("description", org.hamcrest.Matchers.equalTo(product.getDescription()))
                .body("category", org.hamcrest.Matchers.equalTo(product.getCategory()))
                .body("image", org.hamcrest.Matchers.equalTo(product.getImage()))
                .body("price", org.hamcrest.Matchers.equalTo(product.getPrice()));

        wm.verify(postRequestedFor(urlEqualTo("/products"))
                .withHeader("Content-Type", equalTo("application/json"))
                .withRequestBody(equalToJson(body)));
    }


    @Test
    @DisplayName("Create a new product in the FakeStore API using WireMock stubs")
    void createProduct2() {
        String expectedRequest =
        """
        {
          "title": "Test Product",
          "description": "A test product",
          "category": "electronics",
          "image": "https://example.com/image.jpg",
          "price": 19.99
        }
        """;

        String responseBody =
        """
        {
          "id": 101,
          "title": "Test Product",
          "description": "A test product",
          "category": "electronics",
          "image": "https://example.com/image.jpg",
          "price": 19.99
        }
        """;
        // Stub for the Fakestore API endpoint to return a successful response for product creation
        wm.stubFor(post(urlEqualTo("/products"))
                .withHeader("Content-Type", equalTo("application/json"))
                .withRequestBody(equalToJson(expectedRequest))
                .willReturn(aResponse()
                        .withStatus(201)
                        .withHeader("Content-Type", "application/json")
                        .withBody(responseBody)));

        // Call the Fakestore API client to create the product and verify the response
        FakestoreApiClient client = new FakestoreApiClient(given().baseUri(wm.baseUrl()).contentType("application/json").config((config().jsonConfig(
                jsonConfig().numberReturnType(BIG_DECIMAL)))));

        client.createProduct(expectedRequest)
                .then().log().ifValidationFails()
                .assertThat()
                .statusCode(201)
                .contentType("application/json")
                .body("id", org.hamcrest.Matchers.equalTo(101))
                .body("title", org.hamcrest.Matchers.equalTo("Test Product"))
                .body("description", org.hamcrest.Matchers.equalTo("A test product"))
                .body("category", org.hamcrest.Matchers.equalTo("electronics"))
                .body("image", org.hamcrest.Matchers.equalTo("https://example.com/image.jpg"))
                .body("price", org.hamcrest.Matchers.comparesEqualTo(new BigDecimal("19.99")));

        wm.verify(1, postRequestedFor(urlEqualTo("/products"))
                .withHeader("Content-Type", equalTo("application/json"))
                .withRequestBody(equalToJson(expectedRequest)));
    }
}