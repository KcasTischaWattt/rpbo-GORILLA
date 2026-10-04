package com.backend.crosswords.corpus.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "DTO for getting packages for a document")
public class PackagesForDocDTO {
    @Schema(
            example = "Избранное"
    )
    private String name;
    @Schema(
            description = "The flag that shows if the document is included in this package",
            example = "true"
    )
    @JsonProperty("is_included")
    private Boolean isIncluded;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getIsIncluded() {
        return isIncluded;
    }

    public void setIsIncluded(Boolean included) {
        isIncluded = included;
    }

    @Override
    public String toString() {
        return "GetPackagesForDocDTO{" +
                "name='" + name + '\'' +
                ", isIncluded=" + isIncluded +
                '}';
    }
}
