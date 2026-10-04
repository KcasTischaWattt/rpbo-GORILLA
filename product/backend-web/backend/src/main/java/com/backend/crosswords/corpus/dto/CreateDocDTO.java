package com.backend.crosswords.corpus.dto;

import com.backend.crosswords.config.RawTimestampDeserializer;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.swagger.v3.oas.annotations.media.Schema;

import java.sql.Timestamp;
import java.util.List;

@Schema(description = "DTO for creating a document")
public class CreateDocDTO {
    @Schema(
            example = "Международная федерация спортивного кино и телевидения (FICTS) назвала «Матч ТВ» лучшим спортивным телеканалом года."
    )
    private String summary;

    @Schema(
            description = "The date of creation in the ISO format",
            example = "2025-02-26T14:20:00Z"
    )
    @JsonDeserialize(using = RawTimestampDeserializer.class)
    private Timestamp date;

    @Schema(
            description = "URL of the source",
            example = "https://www.interfax.ru/business/1001194"
    )
    @JsonProperty("URL")
    private String url;

    @Schema(
            example = "RU"
    )
    private String language;

    @Schema(
            example = "Интерфакс"
    )
    @JsonProperty("source")
    private String rusSource;

    @Schema(
            example = "Международная федерация спорта признала прошедший матч по футболу величайшим матчем за всю историю."
    )
    private String text;

    @Schema(
            example = "«Матч ТВ» стал лучшим спортивным телеканалом года по версии FICTS"
    )
    private String title;

    @Schema(
            description = "Tags that corresponds with document",
            example = "[\"Спорт\", \"СБП\", \"IT\"]"
    )
    @JsonProperty("tags")
    private List<TagDTO> tagDTOs;

    public CreateDocDTO() {
    }

    public CreateDocDTO(String summary, Timestamp date, String url, String language, String rusSource, String text, String title, List<TagDTO> tags) {
        this.summary = summary;
        this.date = date;
        this.url = url;
        this.language = language;
        this.rusSource = rusSource;
        this.text = text;
        this.title = title;
        this.tagDTOs = tags;
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

    public List<TagDTO> getTagDTOs() {
        return tagDTOs;
    }

    public void setTagDTOs(List<TagDTO> tagDTOs) {
        this.tagDTOs = tagDTOs;
    }

    @Override
    public String toString() {
        return "CreateDocDTO{" +
                "summary='" + summary + '\'' +
                ", date=" + date +
                ", url='" + url + '\'' +
                ", language='" + language + '\'' +
                ", rusSource='" + rusSource + '\'' +
                ", text='" + text + '\'' +
                ", title='" + title + '\'' +
                ", tags=" + tagDTOs +
                '}';
    }
}
