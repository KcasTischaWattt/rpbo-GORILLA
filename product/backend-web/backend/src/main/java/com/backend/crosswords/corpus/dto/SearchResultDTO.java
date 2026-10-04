package com.backend.crosswords.corpus.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "DTO for returning search result")
public class SearchResultDTO {
    @Schema(
            description = "The next page for pagination, if -1, then there is no more docs left",
            example = "1"
    )
    @JsonProperty("next_page")
    Integer nextPage;
    @Schema(
            description = "The flag for checking if user is authenticated",
            example = "true"
    )
    @JsonProperty("is_authed")
    Boolean isAuthed;
    @JsonProperty("documents")
    List<DocDTO> docDTOs;

    public SearchResultDTO() {
    }

    public SearchResultDTO(Integer nextPage, Boolean isAuthed, List<DocDTO> docDTOs) {
        this.nextPage = nextPage;
        this.isAuthed = isAuthed;
        this.docDTOs = docDTOs;
    }

    public Integer getNextPage() {
        return nextPage;
    }

    public void setNextPage(Integer nextPage) {
        this.nextPage = nextPage;
    }

    public Boolean getIsAuthed() {
        return isAuthed;
    }

    public void setIsAuthed(Boolean authed) {
        isAuthed = authed;
    }

    public List<DocDTO> getDocDTOs() {
        return docDTOs;
    }

    public void setDocDTOs(List<DocDTO> docDTOs) {
        this.docDTOs = docDTOs;
    }

    @Override
    public String toString() {
        return "SearchResultDTO{" +
                "nextPage=" + nextPage +
                ", isAuthed=" + isAuthed +
                ", docDTOs=" + docDTOs +
                '}';
    }
}
