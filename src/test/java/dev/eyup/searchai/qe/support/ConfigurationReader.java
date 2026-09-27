package dev.eyup.searchai.qe.support;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

public final class ConfigurationReader {

    private static final Properties PROPERTIES = new Properties();

    static {
        Path configPath = Path.of("configuration.properties");
        try (Reader reader = Files.newBufferedReader(configPath)) {
            PROPERTIES.load(reader);
        } catch (IOException e) {
            throw new IllegalStateException(
                "Unable to read configuration file: " + configPath.toAbsolutePath(), e);
        }
    }

    private ConfigurationReader() {
    }

    /** Returns the configured value, or null if the key is absent. */
    public static String getProperty(String key) {
        return PROPERTIES.getProperty(key);
    }
}
