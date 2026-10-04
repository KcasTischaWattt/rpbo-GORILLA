package com.backend.crosswords.admin.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "DTO to login a user")
public class LoginUserDTO {
    @Schema(
            description = "The user's username, that can be his email or username",
            examples = "geka2003@mail.ru"
    )
    private String username;
    @Schema(
            example = "12345"
    )
    private String password;

    @JsonProperty("fcm_token")
    private String fcmToken;

    public LoginUserDTO(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public LoginUserDTO() {
    }

    public LoginUserDTO(String username, String password, String fcmToken) {
        this.username = username;
        this.password = password;
        this.fcmToken = fcmToken;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFcmToken() {
        return fcmToken;
    }

    public void setFcmToken(String fcmToken) {
        this.fcmToken = fcmToken;
    }

    @Override
    public String toString() {
        return "LoginUserDTO{" +
                "username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", fcmToken='" + fcmToken + '\'' +
                '}';
    }
}
