package dev.eyup.searchai.qe.client;

import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import static io.restassured.RestAssured.given;

public class WeatherApiClient {

    private RequestSpecification requestSpec;

    public WeatherApiClient(RequestSpecification requestSpec){
        this.requestSpec = requestSpec;
    }

    public Response search(String query){

        RequestSpecification request = given()
                .spec(requestSpec);
        if(query != null ) request.queryParam("q", query);

        return request
                .when()
                .get("/search.json");
    }

    public Response getCurrentWeather(String query){
        RequestSpecification request = given()
                .spec(requestSpec);
        if(query != null ) request.queryParam("q", query);

        return request
                .when()
                .get("/current.json");
    }
}
