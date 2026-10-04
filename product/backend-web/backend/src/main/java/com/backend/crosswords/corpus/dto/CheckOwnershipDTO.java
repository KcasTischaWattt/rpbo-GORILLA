package com.backend.crosswords.corpus.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "DTO for checking if user is an owner of the digest subscription")
public class CheckOwnershipDTO {
    @Schema(example = "true")
    @JsonProperty("is_owner")
    private Boolean isOwner;

    public CheckOwnershipDTO() {
    }

    public CheckOwnershipDTO(Boolean isOwner) {
        this.isOwner = isOwner;
    }

    public Boolean getIsOwner() {
        return isOwner;
    }

    public void setIsOwner(Boolean isOwner) {
        this.isOwner = isOwner;
    }

    @Override
    public String toString() {
        return "CheckOwnershipDTO{" +
                "isOwner=" + isOwner +
                '}';
    }
}
