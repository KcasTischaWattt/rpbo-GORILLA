package com.backend.crosswords.admin.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

@Deprecated
public class RefreshUserDTO {
    @JsonProperty("token")
    String refreshToken;

    public RefreshUserDTO(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public RefreshUserDTO() {
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    @Override
    public String toString() {
        return "RefreshUserDTO{" +
                "refreshToken='" + refreshToken + '\'' +
                '}';
    }
}
