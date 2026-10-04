package com.backend.crosswords.corpus.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "DTO for setting followers into digest's subscription")
public class FollowerDTO {
    @Schema(example = "admin")
    private String username;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public String toString() {
        return "FollowerDTO{" +
                "username='" + username + '\'' +
                '}';
    }
}
