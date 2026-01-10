package com.coderiverside.quicknote.config;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("app.lists")
public record SystemLists(List<SystemItem> items) {
    
}
