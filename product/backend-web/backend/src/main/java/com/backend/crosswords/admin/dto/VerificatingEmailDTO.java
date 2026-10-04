package com.backend.crosswords.admin.dto;

public class VerificatingEmailDTO {
    private String email;

    public VerificatingEmailDTO() {
    }

    public VerificatingEmailDTO(String email) {
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
        return "VerificatingEmailDTO{" +
                "email='" + email + '\'' +
                '}';
    }
}
