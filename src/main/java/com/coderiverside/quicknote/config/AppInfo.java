package com.coderiverside.quicknote.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("app.info")
public record AppInfo(String name, String description, String version) {
    
}
