package com.backend.crosswords.corpus.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

import java.sql.Timestamp;
import java.util.List;

@Schema(description = "DTO for editing document")
public class EditDocDTO {
    @Schema(
            example = "Нечто страшное"
    )
    private String title;

    @Schema(
            example = "Интерфакс"
    )
    @JsonProperty("source")
    private String rusSource;

    @Schema(
            example = "Нечто страшное"
    )
    private String summary;

    @Schema(
            example = "Нечто страшное"
    )
    private String text;

    @Schema(
            description = "The date of creation in the ISO format",
            example = "2024-02-26T14:20:00Z"
    )
    private Timestamp date;

    @Schema(
            description = "Flag for dropping down a summary rating",
            example = "true"
    )
    @JsonProperty("drop_rating_summary")
    private Boolean dropRatingSummary;

    @Schema(
            description = "Flag for dropping down a classification rating",
            example = "true"
    )
    @JsonProperty("drop_rating_classification")
    private Boolean dropRatingClassification;

    @Schema(
            example = "RU"
    )
    private String language;

    @Schema(
            description = "URL of the source",
            example = "https://www.interfax.ru/business/1001194"
    )
    @JsonProperty("URL")
    private String url;

    @Schema(
            description = "Tags that corresponds with document",
            example = "[\"Финансы\", \"Спорт\"]"
    )
    @JsonProperty("tags")
    private List<TagDTO> tagDTOs;

    public EditDocDTO(String title, String source, String summary, String text, Timestamp date, Boolean dropRatingSummary, Boolean dropRatingClassification, String language, String url, List<TagDTO> tags) {
        this.title = title;
        this.rusSource = source;
        this.summary = summary;
        this.text = text;
        this.date = date;
        this.dropRatingSummary = dropRatingSummary;
        this.dropRatingClassification = dropRatingClassification;
        this.language = language;
        this.url = url;
        this.tagDTOs = tags;
    }

    public EditDocDTO() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getRusSource() {
        return rusSource;
    }

    public void setRusSource(String rusSource) {
        this.rusSource = rusSource;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Timestamp getDate() {
        return date;
    }

    public void setDate(Timestamp date) {
        this.date = date;
    }

    public Boolean getDropRatingSummary() {
        return dropRatingSummary;
    }

    public void setDropRatingSummary(Boolean dropRatingSummary) {
        this.dropRatingSummary = dropRatingSummary;
    }

    public Boolean getDropRatingClassification() {
        return dropRatingClassification;
    }

    public void setDropRatingClassification(Boolean dropRatingClassification) {
        this.dropRatingClassification = dropRatingClassification;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public List<TagDTO> getTagDTOs() {
        return tagDTOs;
    }

    public void setTagDTOs(List<TagDTO> tagDTOs) {
        this.tagDTOs = tagDTOs;
    }

    @Override
    public String toString() {
        return "EditDocDTO{" +
                "title='" + title + '\'' +
                ", source='" + rusSource + '\'' +
                ", summary='" + summary + '\'' +
                ", text='" + text + '\'' +
                ", date=" + date +
                ", dropRatingSummary=" + dropRatingSummary +
                ", dropRatingClassification=" + dropRatingClassification +
                ", language='" + language + '\'' +
                ", url='" + url + '\'' +
                ", tags=" + tagDTOs +
                '}';
    }
}
