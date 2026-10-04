package com.backend.crosswords.corpus.services;

import com.backend.crosswords.config.DigestGeneratorProperties;
import com.backend.crosswords.corpus.dto.GenerateDigestDTO;
import com.backend.crosswords.corpus.dto.GenerateDigestResponseDTO;
import org.apache.http.ConnectionClosedException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Service
public class DigestGeneratorService {
    private final WebClient webClient;
    private final DigestGeneratorProperties properties;

    public DigestGeneratorService(@Qualifier("digestGeneratorWebClient")WebClient webClient, DigestGeneratorProperties properties) {
        this.webClient = webClient;
        this.properties = properties;
    }
    public Mono<String> generateDigest(GenerateDigestDTO request) {
        return webClient.post()
                .uri(properties.getGenerateDigestPath())
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .retrieve()
                .bodyToMono(GenerateDigestResponseDTO.class)
                .timeout(Duration.ofMillis(properties.getResponseTimeout()))
                .map(GenerateDigestResponseDTO::getResponse)
                .onErrorResume(e -> Mono.error(new ConnectionClosedException(e.getMessage())));
    }
}
