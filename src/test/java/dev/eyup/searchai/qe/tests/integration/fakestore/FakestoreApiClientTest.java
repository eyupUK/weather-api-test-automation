package dev.eyup.searchai.qe.tests.integration.fakestore;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import dev.eyup.searchai.qe.client.FakestoreApiClient;
import dev.eyup.searchai.qe.model.request.CreateProductRequest;
import dev.eyup.searchai.qe.model.response.FakestoreProductResponse;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.*;

import java.math.BigDecimal;
import java.net.SocketTimeoutException;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static dev.eyup.searchai.qe.assertions.FakestoreApiErrorAssertion.assertServiceUnavailableError;
import static io.restassured.RestAssured.given;
import static io.restassured.config.HttpClientConfig.httpClientConfig;
import static io.restassured.config.JsonConfig.jsonConfig;
import static io.restassured.config.LogConfig.logConfig;
import static io.restassured.config.RestAssuredConfig.config;
import static io.restassured.path.json.config.JsonPathConfig.NumberReturnType.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Tag("stub")
class FakestoreApiClientTest {

    private static final ObjectMapper mapper = new ObjectMapper();

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
    @DisplayName("Create a new product in the FakeStore API using WireMock stubs via POJO")
    void shouldSendProductDetailsAndReturnCreatedProductPOJO() throws JsonProcessingException {
        // Implement test logic for creating a product using the Fakestore API client
        CreateProductRequest product = new CreateProductRequest("Test Product", "A test product", "electronics", "https://example.com/image.jpg", new BigDecimal("19.99"));

        // Serialize the request POJO into JSON.
        String body = mapper.writeValueAsString(product);

        // Create a separate JSON object for the response.
        // This does not modify the request POJO.
        ObjectNode responseNode = mapper.valueToTree(product);
        responseNode.put("id", 101);

        String responseBody = mapper.writeValueAsString(responseNode);

        wm.stubFor(post(urlEqualTo("/products"))
                .withHeader("Content-Type", WireMock.equalTo("application/json"))
                .withRequestBody(equalToJson(body))
                .willReturn(aResponse()
                        .withStatus(201)
                        .withHeader("Content-Type", "application/json")
                        .withBody(responseBody)));

         // Call the Fakestore API client to create the product and verify the response
        FakestoreApiClient client = new FakestoreApiClient(given().baseUri(wm.baseUrl()).contentType("application/json").config((config().jsonConfig(
                jsonConfig().numberReturnType(BIG_DECIMAL)))));

        client.createProduct(product)
                .then().log().ifValidationFails()
                .assertThat()
                .statusCode(201)
                .contentType("application/json")
                .body("id", Matchers.instanceOf(Integer.class))
                .body("title", Matchers.equalTo(product.getTitle()))
                .body("description", Matchers.equalTo(product.getDescription()))
                .body("category", Matchers.equalTo(product.getCategory()))
                .body("image", Matchers.equalTo(product.getImage()))
                .body("price", Matchers.comparesEqualTo(product.getPrice()));

        wm.verify(1, postRequestedFor(urlEqualTo("/products"))
                .withHeader("Content-Type", WireMock.equalTo("application/json"))
                .withRequestBody(equalToJson(body)));
    }


    @Test
    @DisplayName("Create a new product in the FakeStore API using WireMock stubs via JSON string")
    void shouldSendProductDetailsAndReturnCreatedProductJSONString() {
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
                .withHeader("Content-Type", WireMock.equalTo("application/json"))
                .withRequestBody(equalToJson(expectedRequest))
                .willReturn(aResponse()
                        .withStatus(201)
                        .withHeader("Content-Type", "application/json")
                        .withBody(responseBody)));

        // Call the Fakestore API client to create the product and verify the response
        FakestoreApiClient client = new FakestoreApiClient(given().baseUri(wm.baseUrl()).contentType("application/json").config((config().jsonConfig(
                jsonConfig().numberReturnType(BIG_INTEGER)))));

        Response response = client.createProduct(expectedRequest);

        response.then().log().ifValidationFails()
                .assertThat()
                .statusCode(201)
                .contentType("application/json");

        FakestoreProductResponse product = response.as(FakestoreProductResponse.class);

        assertEquals(101, product.getId());
        assertEquals("Test Product", product.getTitle());
        assertEquals("A test product", product.getDescription());
        assertEquals("electronics", product.getCategory());
        assertEquals("https://example.com/image.jpg", product.getImage());
        assertEquals(new BigDecimal("19.99"), product.getPrice());


        wm.verify(1, postRequestedFor(urlEqualTo("/products"))
                .withHeader("Content-Type", WireMock.equalTo("application/json"))
                .withRequestBody(equalToJson(expectedRequest)));
    }

    @Test
    @DisplayName("Client exposes a service-unavailable response")
    void shouldReturnServiceUnavailableResponseWithoutRetrying() {
        String body =
                """
                {
                  "title": "Test Product",
                  "description": "A test product",
                  "category": "electronics",
                  "image": "https://example.com/image.jpg",
                  "price": 19.99
                }
                """;

        String errorBody =
                """
                {
                  "error": {
                    "code": "SERVICE_UNAVAILABLE",
                    "message": "Product service is temporarily unavailable"
                  }
                }
                """;

        wm.stubFor(post(urlEqualTo("/products"))
                .withHeader("Content-Type", WireMock.equalTo("application/json"))
                .withRequestBody(equalToJson(body))
                .willReturn(
                        aResponse()
                                .withStatus(503)
                                .withHeader("Retry-After", "120")
                                .withHeader("Content-Type", "application/json")
                                .withBody(errorBody)
                )
        );

        // Call the Fakestore API client to create the product and verify the response
        FakestoreApiClient client = new FakestoreApiClient(
                given()
                        .baseUri(wm.baseUrl())
                        .contentType("application/json")
        );

        Response response = client.createProduct(body);
        assertServiceUnavailableError(response);

        wm.verify(1, postRequestedFor(urlEqualTo("/products"))
                .withHeader("Content-Type", WireMock.equalTo("application/json"))
                .withRequestBody(equalToJson(body)));
    }

    @Test
    @DisplayName("Client times out when product creation responds too slowly")
    void shouldTimeOutWhenProductServiceIsSlow() {

        String body =
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

        wm.stubFor(post(urlEqualTo("/products"))
                .withHeader("Content-Type", WireMock.equalTo("application/json"))
                .withRequestBody(equalToJson(body))
                .willReturn(
                        aResponse()
                                .withStatus(201)
                                .withHeader("Content-Type", "application/json")
                                .withBody(responseBody)
                                .withFixedDelay(3000)
                )
        );


        FakestoreApiClient client = new FakestoreApiClient(
                given()
                        .baseUri(wm.baseUrl())
                        .contentType(ContentType.JSON)
                        .config(config()
                                .httpClient(httpClientConfig()
                                        .setParam("http.socket.timeout", 500))
                                .logConfig(logConfig()
                                        .blacklistDefaultSensitiveHeaders()
                                        .enableLoggingOfRequestAndResponseIfValidationFails()))
        );


        // Execute the request and assert that waiting for the response times out.
        assertThrows(
                SocketTimeoutException.class,
                () -> client.createProduct(body),
                "Expected a read timeout before the delayed response arrives"
        );

        wm.verify(1, postRequestedFor(urlEqualTo("/products"))
                .withHeader("Content-Type", WireMock.equalTo("application/json"))
                .withRequestBody(equalToJson(body)));

    }

}