package com.fsolsh.spring.gateway.bean;

import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Auth配置
 */
@Setter
@RefreshScope
@ConfigurationProperties(prefix = "auth.config")
@Configuration
public class AuthProperties {

    private static final String[] ENDPOINTS = {
            "/oauth/**",
            "/actuator/**",
            "/v2/api-docs/**",
            "/v2/api-docs-ext/**",
            "/swagger/api-docs",
            "/swagger-ui.html",
            "/doc.html",
            "/swagger-resources/**",
            "/webjars/**",
            "/druid/**",
            "/error/**",
            "/assets/**",
            "/auth/logout",
            "/auth/code"
    };

    private List<String> ignoreUrl = new ArrayList<>();

    private Boolean enable = false;

    public List<String> getIgnoreUrl() {
        if (!ignoreUrl.contains("/doc.html")) {
            Collections.addAll(ignoreUrl, ENDPOINTS);
        }
        return ignoreUrl;
    }

    public Boolean getEnable() {
        return enable;
    }
}
