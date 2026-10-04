package com.backend.crosswords.config;

import com.google.auth.oauth2.GoogleCredentials;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;
import java.util.List;

@Configuration
public class FireBaseConfig {
    @Value("${crosswords-firebase-credentials}")
    private String firebaseCredentialsBase64;
    @Bean
    public GoogleCredentials googleCredentials() throws IOException {
        byte[] decoded = Base64.getDecoder().decode(firebaseCredentialsBase64);
        try (InputStream stream = new ByteArrayInputStream(decoded)) {
            return GoogleCredentials.fromStream(stream)
                    .createScoped(List.of("https://www.googleapis.com/auth/firebase.messaging"));
        }
    }
    @Bean
    public WebClient firebaseWebClient(GoogleCredentials googleCredentials) {
        return WebClient.builder()
                .baseUrl("https://fcm.googleapis.com/v1")
                .filter((request, next) -> {
                    try {
                        googleCredentials.refreshIfExpired();
                        String token = googleCredentials.getAccessToken().getTokenValue();

                        return next.exchange(
                                ClientRequest.from(request)
                                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                                        .build()
                        );
                    } catch (IOException e) {
                        return Mono.error(e);
                    }
                })
                .build();
    }
}
