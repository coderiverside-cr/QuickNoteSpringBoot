package com.coderiverside.quicknote.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import java.util.List;

@ConfigurationProperties(prefix = "app.lists")
public record SystemLists(List<SystemItem> items) {
}
