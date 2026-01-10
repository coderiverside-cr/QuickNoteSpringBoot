package com.coderiverside.quicknote.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.coderiverside.quicknote.config.AppInfo;
import com.coderiverside.quicknote.config.FeatureDetail;
import com.coderiverside.quicknote.config.FeatureSettings;
import com.coderiverside.quicknote.config.SystemItem;
import com.coderiverside.quicknote.config.SystemLists;

@RestController
@RequestMapping("/api/config")
public class ConfigController {

    private final AppInfo appInfo;
    private final SystemLists systemLists;
    private final FeatureSettings featureSettings;

    @Value("${app.welcome-message}")
    private String welcomeMessage;

    public ConfigController(AppInfo appInfo, SystemLists systemLists, FeatureSettings featureSettings) {
        this.appInfo = appInfo;
        this.systemLists = systemLists;
        this.featureSettings = featureSettings;
    }

    @GetMapping("/info")
    public AppInfo getAppInfo() {
        return appInfo;
    }

    @GetMapping({ "/list"})
    public List<SystemItem> getWhitelist() {
        return systemLists.items();
    }

    @GetMapping("/features")
    public Map<String, FeatureDetail> getFeatures() {
        return featureSettings.flags();
    }

    @GetMapping("/message")
    public String getWelcomeMessage() {
        return welcomeMessage;
    }
}
