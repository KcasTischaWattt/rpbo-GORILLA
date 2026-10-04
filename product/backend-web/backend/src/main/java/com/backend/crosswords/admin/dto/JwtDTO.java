package com.backend.crosswords.admin.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

@Deprecated
public class JwtDTO {
    @JsonProperty("access_token")
    private String accessToken;
    @JsonProperty("refresh_token")
    private String refreshToken;

    public JwtDTO() {
    }

    public JwtDTO(String accessJwt, String refreshJwt) {
        this.accessToken = accessJwt;
        this.refreshToken = refreshJwt;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    @Override
    public String toString() {
        return "JwtDTO{" +
                "accessJwt='" + accessToken + '\'' +
                ", refreshJwt='" + refreshToken + '\'' +
                '}';
    }
}
