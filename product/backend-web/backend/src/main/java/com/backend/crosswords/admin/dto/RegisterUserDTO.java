package com.backend.crosswords.admin.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "DTO for registering a user")
public class RegisterUserDTO {
    @Schema(
            example = "Гека"
    )
    private String name;

    @Schema(
            example = "Гека"
    )
    private String surname;

    @Schema(
            example = "12345"
    )
    private String password;

    @Schema(
            description = "User's email, can be used to login",
            example = "geka2003@mail.ru"
    )
    private String email;

    @JsonProperty("fcm_token")
    private String fcmToken;

    public RegisterUserDTO(String name, String surname, String password, String email) {
        this.name = name;
        this.surname = surname;
        this.password = password;
        this.email = email;
    }

    public RegisterUserDTO() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFcmToken() {
        return fcmToken;
    }

    public void setFcmToken(String fcmToken) {
        this.fcmToken = fcmToken;
    }

    @Override
    public String toString() {
        return "RegisterUserDTO{" +
                "name='" + name + '\'' +
                ", surname='" + surname + '\'' +
                ", password='" + password + '\'' +
                ", email='" + email + '\'' +
                ", fcmToken='" + fcmToken + '\'' +
                '}';
    }
}
