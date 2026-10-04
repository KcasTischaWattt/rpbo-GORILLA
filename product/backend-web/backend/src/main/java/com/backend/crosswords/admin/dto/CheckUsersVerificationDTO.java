package com.backend.crosswords.admin.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CheckUsersVerificationDTO {
    @JsonProperty("needs_confirmation")
    private Boolean needsConfirmation;

    public CheckUsersVerificationDTO(Boolean needsConfirmation) {
        this.needsConfirmation = needsConfirmation;
    }

    public CheckUsersVerificationDTO() {
    }

    public Boolean getNeedsConfirmation() {
        return needsConfirmation;
    }

    public void setNeedsConfirmation(Boolean needsConfirmation) {
        this.needsConfirmation = needsConfirmation;
    }

    @Override
    public String toString() {
        return "CheckUsersVerificationDTO{" +
                "needsConfirmation=" + needsConfirmation +
                '}';
    }
}
