package com.coderiverside.quicknote.controller;

import com.coderiverside.quicknote.config.AppInfo;
import com.coderiverside.quicknote.config.FeatureDetail;
import com.coderiverside.quicknote.config.FeatureSettings;
import com.coderiverside.quicknote.config.SystemItem;
import com.coderiverside.quicknote.config.SystemLists;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/config")
public class ConfigController {

    private final AppInfo appInfo;
    private final FeatureSettings featureSettings;
    private final SystemLists systemLists;

    @Value("${app.welcome-message}")
    private String welcomeMessage;

    public ConfigController(AppInfo appInfo, FeatureSettings featureSettings, SystemLists systemLists) {
        this.appInfo = appInfo;
        this.featureSettings = featureSettings;
        this.systemLists = systemLists;
    }

    @GetMapping("/info")
    public AppInfo getAppInfo() {
        return appInfo;
    }

    @GetMapping("/features")
    public Map<String, FeatureDetail> getFeatures() {
        return featureSettings.flags();
    }

    @GetMapping("/whitelist")
    public List<SystemItem> getWhitelist() {
        return systemLists.items();
    }

    @GetMapping("/message")
    public String getWelcomeMessage() {
        return welcomeMessage;
    }
}
