package com.backend.crosswords.corpus.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

import java.sql.Timestamp;

public class SubscriptionsDigestDTO {
    @Schema(example = "1-1")
    private String id;
    @Schema(example = "4.6")
    @JsonProperty("average_rating")
    private Double averageRating;
    @Schema(example = "05/03/2025")
    @JsonFormat(pattern = "dd/MM/yyyy")
    private Timestamp date;
    @Schema(example = "Нечто страшное")
    private String text;

    public SubscriptionsDigestDTO() {
    }

    public SubscriptionsDigestDTO(String id, Double averageRating, Timestamp date, String text) {
        this.id = id;
        this.averageRating = averageRating;
        this.date = date;
        this.text = text;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Double getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(Double averageRating) {
        this.averageRating = averageRating;
    }

    public Timestamp getDate() {
        return date;
    }

    public void setDate(Timestamp date) {
        this.date = date;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return "SubscriptionsDigests{" +
                "id='" + id + '\'' +
                ", averageRating=" + averageRating +
                ", date=" + date +
                ", text='" + text + '\'' +
                '}';
    }
}
