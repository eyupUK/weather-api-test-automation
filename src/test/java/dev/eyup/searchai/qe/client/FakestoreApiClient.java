package dev.eyup.searchai.qe.client;

import dev.eyup.searchai.qe.model.request.CreateProductRequest;
import io.restassured.config.LogConfig;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import static io.restassured.RestAssured.config;
import static io.restassured.RestAssured.given;
import static io.restassured.config.LogConfig.logConfig;

public class FakestoreApiClient {

    private RequestSpecification requestSpec;

    public FakestoreApiClient(RequestSpecification requestSpec) {
        this.requestSpec = requestSpec;
    }

    public Response createProduct(CreateProductRequest product) {


        return given()
                .spec(requestSpec)
                .body(product)
                .when()
                .post("/products");
    }

    public Response createProduct(String product) {

        return given()
                .spec(requestSpec)
                .body(product)
                .when()
                .post("/products");
    }
}
