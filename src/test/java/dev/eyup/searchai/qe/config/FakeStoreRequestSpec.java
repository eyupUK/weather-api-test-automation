package dev.eyup.searchai.qe.config;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.config.LogConfig;
import io.restassured.specification.RequestSpecification;

import static io.restassured.config.RestAssuredConfig.config;
import static io.restassured.config.JsonConfig.jsonConfig;
import static io.restassured.path.json.config.JsonPathConfig.NumberReturnType.BIG_DECIMAL;

public class FakeStoreRequestSpec {

    public static RequestSpecification baseSpec(){
        LogConfig logConfig =
                LogConfig.logConfig()
                        .blacklistDefaultSensitiveHeaders()
                        .enableLoggingOfRequestAndResponseIfValidationFails();
        return new RequestSpecBuilder()
                .setConfig(config().jsonConfig(
                        jsonConfig().numberReturnType(BIG_DECIMAL)))
                .setConfig(config().logConfig(logConfig))
                .setAccept("application/json")
                .setContentType("application/json")
                .setBaseUri(FakeStoreApiTestConfig.getBaseUri())
                .build();
    }

    public static RequestSpecification productsSpec(){
        return baseSpec()
                .basePath("/products");
    }
}
