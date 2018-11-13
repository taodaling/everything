package com.daltao.util;

import com.daltao.utils.StringUtils;
import lombok.Data;

import java.io.IOException;
import java.util.Objects;
import java.util.Properties;

@Data
public class ConfigUtils {
    private String readCacheAlgorithm;
    private Long readCacheSize;
    private Long writeCacheSize;
    private String dbDataDirectory;

    private static ConfigUtils instance = new ConfigUtils();

    private ConfigUtils() {
        Properties properties = new Properties();
        try {
            properties.load(ConfigUtils.class.getClassLoader().getResourceAsStream("db-settings.properties"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        readCacheAlgorithm = StringUtils.defaultString(properties.getProperty("read.cache.algorithm"), "lru");
        readCacheSize = Long.parseLong(StringUtils.defaultString(properties.getProperty("read.cache.size"), "1024"));
        writeCacheSize = Long.parseLong(StringUtils.defaultString(properties.getProperty("write.cache.size"), "64"));
        dbDataDirectory = Objects.requireNonNull(properties.getProperty("db.data.directory"));
    }

    public static ConfigUtils getInstance() {
        return instance;
    }
}
