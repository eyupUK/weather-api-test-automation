package dev.eyup.searchai.qe.config;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.config.LogConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;

import static io.restassured.config.LogConfig.logConfig;

public class WeatherApiRequestSpec {

    public static RequestSpecification baseSpecs() {
        WeatherApiTestConfig weatherApiTestConfig = new WeatherApiTestConfig();
        LogConfig logConfig =
                logConfig()
                        .blacklistDefaultSensitiveHeaders()
                        .enableLoggingOfRequestAndResponseIfValidationFails(LogDetail.HEADERS);
        RestAssuredConfig restAssuredConfig =
                RestAssuredConfig.config()
                        .logConfig(
                                logConfig
                        );

        return new RequestSpecBuilder()
                .setBaseUri(weatherApiTestConfig.getBaseUri())
                .setAccept(ContentType.JSON)
                .setConfig(restAssuredConfig)
                .setBasePath("/v1")
                .build();
    }

    public static RequestSpecification baseSpecsWithApiKey() {

        return new RequestSpecBuilder()
                .addRequestSpecification(baseSpecs())
                .addQueryParam("key", new WeatherApiTestConfig().getApiKey())
                .build();
    }
}