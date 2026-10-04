package com.backend.crosswords.admin.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.ArrayList;
import java.util.List;

@Schema(description = "DTO to return authorities from user")
public class AuthorityDTO {
    @Schema(example = "no_authorities")
    private List<String> authorities;

    public AuthorityDTO() {
        authorities = new ArrayList<>();
    }

    public AuthorityDTO(List<String> authorities) {
        this.authorities = authorities;
    }

    public List<String> getAuthorities() {
        return authorities;
    }

    public void setAuthorities(List<String> authorities) {
        this.authorities = authorities;
    }
}
