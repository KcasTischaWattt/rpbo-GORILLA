package com.backend.crosswords.admin.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "DTO to return user's email")
public class EmailDTO {
    @Schema(example = "admin@mail.ru")
    private String email;

    public EmailDTO() {
    }

    public EmailDTO(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return "EmailDTO{" +
                "email='" + email + '\'' +
                '}';
    }
}
