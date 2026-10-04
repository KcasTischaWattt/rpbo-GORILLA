package com.backend.crosswords.admin.dto;

public class NewEmailAndUsernameDTO {
    private String email;
    private String username;

    public NewEmailAndUsernameDTO() {
    }

    public NewEmailAndUsernameDTO(String email, String username) {
        this.email = email;
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public String toString() {
        return "NewEmailAndUsernameDTO{" +
                "email='" + email + '\'' +
                ", username='" + username + '\'' +
                '}';
    }
}
