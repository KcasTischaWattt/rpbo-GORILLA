package com.backend.crosswords.corpus.services;

import com.backend.crosswords.config.MailmanProperties;
import com.backend.crosswords.corpus.dto.SendDigestByEmailsDTO;
import com.backend.crosswords.corpus.dto.SendVerificationCodeDTO;
import org.apache.http.ConnectionClosedException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class MailManService {
    private final WebClient webClient;
    private final MailmanProperties properties;

    public MailManService(@Qualifier("mailmanWebClient")WebClient webClient, MailmanProperties properties) {
        this.webClient = webClient;
        this.properties = properties;
    }
    public Mono<String> sendEmail(SendDigestByEmailsDTO request) throws ConnectionClosedException {
        return webClient.post()
                .uri(properties.getSendEmailPath())
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .retrieve()
                .onStatus(HttpStatusCode::isError, error -> Mono.error(new ConnectionClosedException("Connection with mailman error")))
                .bodyToMono(String.class);
    }
    public Mono<String> sendVerificationCode(SendVerificationCodeDTO request) throws ConnectionClosedException {
        try {
            return webClient.post()
                    .uri(properties.getVerifyEmailPath())
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(request)
                    .retrieve()
                    .onStatus(HttpStatusCode::isError, error -> Mono.error(new ConnectionClosedException("Connection with mailman error")))
                    .bodyToMono(String.class);
        } catch (Exception e) {
            throw new ConnectionClosedException(e.getMessage());
        }
    }
}
