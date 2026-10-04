package com.backend.crosswords.admin.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "DTO for getting a user by its username")
public class UsernameDTO {
    @Schema(
            description = "The user's username, that can be his email or username",
            examples = "geka2003@mail.ru"
    )
    private String username;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public String toString() {
        return "UsernameDTO{" +
                "username='" + username + '\'' +
                '}';
    }
}
