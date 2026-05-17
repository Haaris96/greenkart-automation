package com.greenkart.utils;

import com.greenkart.constants.Constants;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Singleton config reader. Loads config.properties once; falls back to defaults.
 */
public class ConfigReader {

    private static final Logger log = LogManager.getLogger(ConfigReader.class);
    private static Properties properties;

    private ConfigReader() {}

    private static Properties load() {
        if (properties == null) {
            properties = new Properties();
            String env = System.getProperty("env", "staging");
            String configPath = "src/test/resources/config/" + env + ".properties";

            try (FileInputStream fis = new FileInputStream(configPath)) {
                properties.load(fis);
                log.info("Loaded config from: {}", configPath);
            } catch (IOException e) {
                // Fall back to base config
                try (FileInputStream fis = new FileInputStream(Constants.CONFIG_FILE)) {
                    properties.load(fis);
                    log.warn("Env-specific config not found; loaded base config");
                } catch (IOException ex) {
                    log.error("Cannot load any config file", ex);
                }
            }
        }
        return properties;
    }

    public static String get(String key) {
        String sysVal = System.getProperty(key);
        if (sysVal != null && !sysVal.isBlank()) return sysVal;
        return load().getProperty(key, "");
    }

    public static String get(String key, String defaultValue) {
        String val = get(key);
        return (val == null || val.isBlank()) ? defaultValue : val;
    }

    public static int getInt(String key, int defaultValue) {
        try {
            return Integer.parseInt(get(key));
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    public static boolean getBoolean(String key, boolean defaultValue) {
        String val = get(key);
        return (val == null || val.isBlank()) ? defaultValue : Boolean.parseBoolean(val);
    }
}
