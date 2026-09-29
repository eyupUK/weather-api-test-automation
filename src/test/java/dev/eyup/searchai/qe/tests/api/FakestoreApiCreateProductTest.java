package dev.eyup.searchai.qe.tests.api;

import dev.eyup.searchai.qe.client.FakestoreApiClient;
import dev.eyup.searchai.qe.config.FakeStoreRequestSpec;
import dev.eyup.searchai.qe.model.request.CreateProductRequest;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static dev.eyup.searchai.qe.config.FakeStoreRequestSpec.productsSpec;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@Tag("live")
public class FakestoreApiCreateProductTest {

    @Test
    @DisplayName("Create a new product in the FakeStore API")
    public void createProduct() {
        RequestSpecification requestSpec = given().spec(productsSpec());
        Response response = requestSpec
                .body("{\"title\":\"Test Product\",\"price\":19.99,\"description\":\"A test product\",\"category\":\"electronics\",\"image\":\"https://example.com/image.jpg\"}")
                .post();

        response.then().log().all();
        response.then().assertThat()
                .statusCode(201)
                .contentType("application/json")
                .body("id", greaterThanOrEqualTo(1)).body("id", instanceOf(Integer.class))
                .body("title", equalTo("Test Product"))
                .body("price", equalTo(19.99))
                .body("description", equalTo("A test product"))
                .body("category", equalTo("electronics"))
                .body("image", equalTo("https://example.com/image.jpg"));
    }

    @Test
    @DisplayName("Create a new product in the FakeStore API")
    public void createProductPOJO() {
        CreateProductRequest product = new CreateProductRequest("Test Product", "A test product", "electronics", "https://example.com/image.jpg", new BigDecimal("19.99"));


        Response response = given().spec(productsSpec())
                .body(product)
                .post();

        response.then().log().ifValidationFails()
                .assertThat()
                .statusCode(201)
                .contentType("application/json")
                .body("id", greaterThanOrEqualTo(1)).body("id", instanceOf(Integer.class))
                .body("title", equalTo(product.getTitle()))
                .body("price", equalTo(product.getPrice()))
                .body("description", equalTo(product.getDescription()))
                .body("category", equalTo(product.getCategory()))
                .body("image", equalTo(product.getImage()));
    }


    @Test
    @DisplayName("Create a new product in the FakeStore API with client")
    public void createProductClient() {
        CreateProductRequest product = new CreateProductRequest("Test Product", "A test product", "electronics", "https://example.com/image.jpg", new BigDecimal("19.99"));

        FakestoreApiClient client =
                new FakestoreApiClient(FakeStoreRequestSpec.baseSpec());

        Response response = client.createProduct(product);

//        response.then().log().all();
        response.then().assertThat()
                .statusCode(201)
                .contentType("application/json")
                .body("id", greaterThanOrEqualTo(1)).body("id", instanceOf(Integer.class))
                .body("title", equalTo(product.getTitle()))
                .body("price", equalTo(product.getPrice()))
                .body("description", equalTo(product.getDescription()))
                .body("category", equalTo(product.getCategory()))
                .body("image", equalTo(product.getImage()));
    }
}
