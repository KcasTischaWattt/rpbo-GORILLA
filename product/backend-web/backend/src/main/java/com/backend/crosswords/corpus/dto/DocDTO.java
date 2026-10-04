package com.backend.crosswords.corpus.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

import java.sql.Timestamp;
import java.util.List;

@Schema(description = "DTO to get documents in response by any request")
public class DocDTO {
    @Schema(example = "2")
    private Long id;
    @Schema(example = "Нечто страшное")
    private String title;
    @Schema(example = "Интерфакс")
    @JsonProperty("source")
    private String rusSource;
    @Schema(example = "Нечто страшное")
    private String summary;
    @Schema(example = "Нечто страшное")
    private String text;
    @Schema(example = "[\n" +
            "            \"Спорт\",\n" +
            "            \"Финансы\"\n" +
            "        ]")
    @JsonProperty("tags")
    private List<String> tagNames;
    @Schema(example = "01/01/2003")
    @JsonFormat(pattern = "dd/MM/yyyy")
    private Timestamp date;
    @Schema(example = "05/03/2025")
    @JsonFormat(pattern = "dd/MM/yyyy")
    @JsonProperty("last_edit")
    private Timestamp lastEdit;
    @Schema(example = "1", description = "null, or a number between 1 and 5")
    @JsonProperty("rating_summary")
    private Integer ratingSummary;
    @Schema(example = "1", description = "null, or a number between 1 and 5")
    @JsonProperty("rating_classification")
    private Integer ratingClassification;
    @Schema(example = "true", description = "check if authed user added this doc into favourites, can be null or true/false")
    private Boolean favourite;
    @Schema(example = "true", description = "check if user is authed")
    @JsonProperty("is_authed")
    private Boolean authed;
    @Schema(example = "RU")
    private String language;
    @Schema(example = "https://www.interfax.ru/business/1001194")
    @JsonProperty("URL")
    private String url;
    @JsonProperty("is_moderator")
    private Boolean isModerator;
    @JsonProperty("annotations")
    private List<AnnotationDTO> docsAnnotations;
    public DocDTO(Long id, String summary, Timestamp date, String url, String language, String text, String title) {
        this.id = id;
        this.summary = summary;
        this.date = date;
        this.url = url;
        this.language = language;
        this.text = text;
        this.title = title;
    }

    public DocDTO() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public Timestamp getDate() {
        return date;
    }

    public void setDate(Timestamp date) {
        this.date = date;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
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

    public List<String> getTagNames() {
        return tagNames;
    }

    public void setTagNames(List<String> tagNames) {
        this.tagNames = tagNames;
    }

    public Timestamp getLastEdit() {
        return lastEdit;
    }

    public void setLastEdit(Timestamp lastEdit) {
        this.lastEdit = lastEdit;
    }

    public Integer getRatingSummary() {
        return ratingSummary;
    }

    public void setRatingSummary(Integer ratingSummary) {
        this.ratingSummary = ratingSummary;
    }

    public Integer getRatingClassification() {
        return ratingClassification;
    }

    public void setRatingClassification(Integer ratingClassification) {
        this.ratingClassification = ratingClassification;
    }

    public Boolean getFavourite() {
        return favourite;
    }

    public void setFavourite(Boolean favourite) {
        this.favourite = favourite;
    }

    public Boolean getAuthed() {
        return authed;
    }

    public void setAuthed(Boolean authed) {
        this.authed = authed;
    }

    public List<AnnotationDTO> getDocsAnnotations() {
        return docsAnnotations;
    }

    public void setDocsAnnotations(List<AnnotationDTO> docsAnnotations) {
        this.docsAnnotations = docsAnnotations;
    }

    public Boolean getIsModerator() {
        return isModerator;
    }

    public void setIsModerator(Boolean moderator) {
        isModerator = moderator;
    }

    @Override
    public String toString() {
        return "DocDTO{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", rusSource='" + rusSource + '\'' +
                ", summary='" + summary + '\'' +
                ", text='" + text + '\'' +
                ", tagNames=" + tagNames +
                ", date=" + date +
                ", lastEdit=" + lastEdit +
                ", ratingSummary=" + ratingSummary +
                ", ratingClassification=" + ratingClassification +
                ", favourite=" + favourite +
                ", authed=" + authed +
                ", language='" + language + '\'' +
                ", url='" + url + '\'' +
                ", isModerator=" + isModerator +
                ", docsAnnotations=" + docsAnnotations +
                '}';
    }
}
