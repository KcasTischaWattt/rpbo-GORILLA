package com.backend.crosswords.corpus.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

import java.sql.Timestamp;
import java.util.List;

@Schema(description = "DTO for searching document, if any filter's arguments are null, then their check automatically approves")
public class SearchDocDTO {

    @Schema(
            example = "ставший лучшим спортом"
    )
    @JsonProperty("search_body")
    private String searchTerm;

    @Schema(
            description = "Search mode ('id'/'semantic'/'certain')",
            example = "semantic"
    )
    @JsonProperty("search_mode")
    private String searchMode;

    @Schema(
            description = "The date of the beginning of the searching period in format yyyy-MM-dd. Can be later then date_to, they would simply exchange values",
            example = "2000-03-25"
    )
    @JsonProperty("date_from")
    private Timestamp dateFrom;

    @Schema(
            description = "The date of the end of the searching period in format yyyy-MM-dd. Can be before then date_from, they would simply exchange values",
            example = "2025-03-25"
    )
    @JsonProperty("date_to")
    private Timestamp dateTo;

    @Schema(
            description = "Page number for pagination (begins from 0). Modified version of the last_sentence_pos",
            example = "0"
    )
    @JsonProperty("next_page")
    private Integer pageNumber;

    @Schema(
            description = "The number of results on the page",
            example = "20"
    )
    @JsonProperty("matches_per_page")
    private Integer matchesPerPage;

    @JsonProperty("search_in_text")
    private Boolean searchInText;

    @Schema(
            description = "The list of probable languages, if document's language contains in them, then it gets through",
            example = "[\"RU\"]"
    )
    private List<String> language;

    @Schema(
            description = "The list of probable sources, if document's source contains in them, then it gets through",
            example = "[\"Интерфакс\"]"
    )
    private List<String> sources;

    @Schema(
            description = "The list of required tags, if document doesn't have at least one of them, then it doesn't get through",
            example = "[\"Спорт\"]"
    )
    private List<String> tags;
    @Schema(
            description = "The list of required packages, if document doesn't contains at least in one of them, then it doesn't get through",
            example = "[\"Избранное\"]"
    )
    private List<String> folders;

    public SearchDocDTO() {
    }

    public SearchDocDTO(List<String> sources, List<String> tags) {
        this.sources = sources;
        this.tags = tags;
    }

    public String getSearchTerm() {
        return searchTerm;
    }

    public void setSearchTerm(String searchTerm) {
        this.searchTerm = searchTerm;
    }

    public String getSearchMode() {
        return searchMode;
    }

    public void setSearchMode(String searchMode) {
        this.searchMode = searchMode;
    }

    public Timestamp getDateFrom() {
        return dateFrom;
    }

    public void setDateFrom(Timestamp dateFrom) {
        this.dateFrom = dateFrom;
    }

    public Timestamp getDateTo() {
        return dateTo;
    }

    public void setDateTo(Timestamp dateTo) {
        this.dateTo = dateTo;
    }

    public List<String> getLanguage() {
        return language;
    }

    public void setLanguage(List<String> language) {
        this.language = language;
    }

    public Integer getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(Integer pageNumber) {
        this.pageNumber = pageNumber;
    }

    public Integer getMatchesPerPage() {
        return matchesPerPage;
    }

    public void setMatchesPerPage(Integer matchesPerPage) {
        this.matchesPerPage = matchesPerPage;
    }

    public List<String> getSources() {
        return sources;
    }

    public void setSources(List<String> sources) {
        this.sources = sources;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public List<String> getFolders() {
        return folders;
    }

    public void setFolders(List<String> folders) {
        this.folders = folders;
    }

    public Boolean getSearchInText() {
        return searchInText;
    }

    public void setSearchInText(Boolean searchInText) {
        this.searchInText = searchInText;
    }

    @Override
    public String toString() {
        return "SearchDocDTO{" +
                "searchTerm='" + searchTerm + '\'' +
                ", searchMode='" + searchMode + '\'' +
                ", dateFrom=" + dateFrom +
                ", dateTo=" + dateTo +
                ", pageNumber=" + pageNumber +
                ", matchesPerPage=" + matchesPerPage +
                ", searchInText=" + searchInText +
                ", language=" + language +
                ", sources=" + sources +
                ", tags=" + tags +
                ", folders=" + folders +
                '}';
    }
}
