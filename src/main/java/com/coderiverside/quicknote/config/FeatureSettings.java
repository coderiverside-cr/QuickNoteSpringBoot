package com.coderiverside.quicknote.config;

import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;


@ConfigurationProperties(prefix = "app.features")
public record FeatureSettings(Map<String, FeatureDetail> flags) {
    
}
