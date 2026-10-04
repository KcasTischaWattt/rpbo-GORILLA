package com.backend.crosswords.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class DigestGeneratorProperties {
    @Value("${digest-generator.url}")
    private String url;
    @Value("${digest-generator.generate-digest-path}")
    private String generateDigestPath;
    @Value("${digest-generator.connect-timeout}")
    private int connectTimeout;
    @Value("${digest-generator.response-timeout}")
    private int responseTimeout;

    public String getUrl() {
        return url;
    }

    public String getGenerateDigestPath() {
        return generateDigestPath;
    }

    public int getConnectTimeout() {
        return connectTimeout;
    }

    public int getResponseTimeout() {
        return responseTimeout;
    }
}
