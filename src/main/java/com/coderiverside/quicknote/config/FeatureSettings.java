package com.coderiverside.quicknote.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import java.util.Map;

@ConfigurationProperties(prefix = "app.features")
public record FeatureSettings(Map<String, FeatureDetail> flags) {
}
