package dev.eyup.searchai.qe.config;

import dev.eyup.searchai.qe.support.ConfigurationReader;

public class FakeStoreApiTestConfig {

    public static String getBaseUri() {
        String uri = System.getenv("FAKESTORE_API_BASE_URI") != null ? System.getenv("FAKESTORE_API_BASE_URI") : ConfigurationReader.getProperty("FAKESTORE_API_BASE_URI");
        if (uri == null || uri.isBlank()) {
            throw new IllegalStateException(
                    "Set FAKESTORE_API_BASE_URI in your terminal or IDE environment."
            );
        }
        return uri;
    }
}
