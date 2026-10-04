package com.backend.crosswords.corpus.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "DTO for rating document")
public class RateDocDTO {
    @Schema(
            example = "3",
            description = "Can be null or a number between 1 and 5"
    )
    @JsonProperty("summary_rating")
    private Integer summaryRating;
    @Schema(
            example = "5",
            description = "Can be null or a number between 1 and 5"
    )
    @JsonProperty("classification_rating")
    private Integer classificationRating;

    public RateDocDTO(Integer summaryRating, Integer classificationRating) {
        this.summaryRating = summaryRating;
        this.classificationRating = classificationRating;
    }

    public RateDocDTO() {
    }

    public Integer getSummaryRating() {
        return summaryRating;
    }

    public void setSummaryRating(Integer summaryRating) {
        this.summaryRating = summaryRating;
    }

    public Integer getClassificationRating() {
        return classificationRating;
    }

    public void setClassificationRating(Integer classificationRating) {
        this.classificationRating = classificationRating;
    }

    @Override
    public String toString() {
        return "RateDocDTO{" +
                "summaryRating=" + summaryRating +
                ", classificationRating=" + classificationRating +
                '}';
    }
}
