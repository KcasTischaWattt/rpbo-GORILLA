package com.backend.crosswords.corpus.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.ArrayList;
import java.util.List;

@Schema(description = "DTO to wrap a list of digests")
public class DigestsDTO {
    @Schema(
            description = "The next page for pagination, if -1, then there is no more docs left",
            example = "1"
    )
    @JsonProperty("next_page")
    Integer nextPage;
    @JsonProperty("is_authed")
    private Boolean isAuthed;
    private List<DigestDTO> digests;

    public DigestsDTO() {
        digests = new ArrayList<>();
    }

    public List<DigestDTO> getDigests() {
        return digests;
    }

    public void setDigests(List<DigestDTO> digests) {
        this.digests = digests;
    }

    public Boolean getIsAuthed() {
        return isAuthed;
    }

    public void setIsAuthed(Boolean isAuthed) {
        this.isAuthed = isAuthed;
    }

    public Integer getNextPage() {
        return nextPage;
    }

    public void setNextPage(Integer nextPage) {
        this.nextPage = nextPage;
    }

    @Override
    public String toString() {
        return "DigestsDTO{" +
                "nextPage=" + nextPage +
                ", isAuthed=" + isAuthed +
                ", digests=" + digests +
                '}';
    }
}
