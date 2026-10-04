package com.backend.crosswords.corpus.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public class ChangeDigestSubscriptionsOwnerDTO {
    @Schema(
            example = "admin"
    )
    private String owner;

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    @Override
    public String toString() {
        return "ChangeDigestSubscriptionOwnerDTO{" +
                "owner='" + owner + '\'' +
                '}';
    }
}
