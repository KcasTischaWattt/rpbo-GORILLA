package com.backend.crosswords.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class TelegramConfig {

    @Value("${telegram.bot.url:http://telegram-bot:9010}")
    private String telegramBotUrl;

    @Bean(name = "telegramWebClient")
    public WebClient telegramWebClient() {
        return WebClient.builder()
                .baseUrl(telegramBotUrl)
                .build();
    }
}