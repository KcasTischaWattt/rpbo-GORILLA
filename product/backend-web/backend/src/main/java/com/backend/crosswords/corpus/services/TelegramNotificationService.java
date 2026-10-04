package com.backend.crosswords.corpus.services;

import org.apache.http.ConnectionClosedException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

@Service
public class TelegramNotificationService {
    private final WebClient telegramWebClient;

    @Value("${telegram.bot.internal-secret}")
    private String internalSecret;

    @Value("${telegram.bot.url:http://telegram-bot:8000}")
    private String telegramBotUrl;

    public TelegramNotificationService(@Qualifier("telegramWebClient") WebClient telegramWebClient) {
        this.telegramWebClient = telegramWebClient;
    }

    public Mono<Void> sendDigestNotification(Long telegramId, String digestId, String title, String text) {
        String url = "/internal/send-digest";

        Map<String, Object> payload = new HashMap<>();
        payload.put("telegramId", telegramId);
        payload.put("digestId", digestId);
        payload.put("title", title);
        payload.put("text", text.length() > 500 ? text.substring(0, 497) + "..." : text);
        payload.put("fullText", text);

        return telegramWebClient.post()
                .uri(url)
                .header("X-Internal-Secret", internalSecret)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(payload)
                .retrieve()
                .bodyToMono(String.class)
                .doOnNext(response -> System.out.println("Telegram notification sent to " + telegramId))
                .then()
                .onErrorResume(e -> {
                    System.err.println("Failed to send Telegram notification: " + e.getMessage());
                    return Mono.empty();
                });
    }
}