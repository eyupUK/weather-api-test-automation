package dev.eyup.searchai.qe.config;

import dev.eyup.searchai.qe.support.ConfigurationReader;


import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class TestConfig {

    public String getApiKey() {
        String apiKey = System.getenv("WEATHER_API_KEY") != null ? System.getenv("WEATHER_API_KEY") : ConfigurationReader.getProperty("WEATHER_API_KEY");
        if (apiKey == null || apiKey.isBlank()) {
            throw new IllegalStateException(
                    "Set WEATHER_API_KEY in your terminal or IDE environment."
            );
        }
        return apiKey;
    }
    public String getProxyUrl() {
        String proxyUrl = System.getenv("WEATHER_API_PROXY") != null ? System.getenv("WEATHER_API_PROXY") : ConfigurationReader.getProperty("WEATHER_API_PROXY");
        return (proxyUrl != null && !proxyUrl.isBlank()) ? proxyUrl : null;
    }
    public String getBaseUri() {
        String uri = System.getenv("WEATHER_API_BASE_URI") != null ? System.getenv("WEATHER_API_BASE_URI") : ConfigurationReader.getProperty("WEATHER_API_BASE_URI");
        if (uri == null || uri.isBlank()) {
            throw new IllegalStateException(
                    "Set WEATHER_API_BASE_URI in your terminal or IDE environment."
            );
        }
        return uri;
    }

}
