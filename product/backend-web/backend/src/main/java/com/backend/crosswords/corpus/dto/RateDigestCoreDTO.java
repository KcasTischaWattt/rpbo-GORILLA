package com.backend.crosswords.corpus.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "DTO for rating digest core")
public class RateDigestCoreDTO {
    @Schema(
            example = "3",
            description = "Can be null or a number between 1 and 5"
    )
    @JsonProperty("digest_core_rating")
    private Integer digestCoreRating;

    public Integer getDigestCoreRating() {
        return digestCoreRating;
    }

    public void setDigestCoreRating(Integer digestCoreRating) {
        this.digestCoreRating = digestCoreRating;
    }

    @Override
    public String toString() {
        return "RateDigestCoreDTO{" +
                "digestCoreRating=" + digestCoreRating +
                '}';
    }
}
