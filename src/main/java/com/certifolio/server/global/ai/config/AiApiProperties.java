package com.certifolio.server.global.ai.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "ai.api")
public record AiApiProperties(
        String baseUrl,
        Integer timeoutSeconds,
        String internalApiKey
) {
    public AiApiProperties {
        if (baseUrl == null || baseUrl.isBlank()) {
            baseUrl = "http://localhost:8001";
        }
        if (timeoutSeconds == null) {
            timeoutSeconds = 20;
        }
        if (internalApiKey == null) {
            internalApiKey = "local-secret";
        }
    }
}
