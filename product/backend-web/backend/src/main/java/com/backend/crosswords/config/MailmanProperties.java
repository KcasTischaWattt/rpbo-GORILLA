package com.backend.crosswords.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class MailmanProperties {
    @Value("${mailman.url}")
    private String url;
    @Value("${mailman.send-email-path}")
    private String sendEmailPath;
    @Value("${mailman.verify-email-path}")
    private String verifyEmailPath;
    @Value("${mailman.connect-timeout}")
    private int connectTimeout;
    @Value("${mailman.response-timeout}")
    private int responseTimeout;

    public String getUrl() {
        return url;
    }

    public String getSendEmailPath() {
        return sendEmailPath;
    }

    public int getConnectTimeout() {
        return connectTimeout;
    }

    public int getResponseTimeout() {
        return responseTimeout;
    }

    public String getVerifyEmailPath() {
        return verifyEmailPath;
    }
}
