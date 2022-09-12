package com.tenerity.nordic.util;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;

@ConfigurationProperties(prefix = "signicat")
public class ConfigProperties {
    private Map<String, String> signicatMethodName;

    public Map<String, String> getSignicatMethodName() {
        return signicatMethodName;
    }

    public void setSignicatMethodName(Map<String, String> signicatMethodName) {
        this.signicatMethodName = signicatMethodName;
    }
}
