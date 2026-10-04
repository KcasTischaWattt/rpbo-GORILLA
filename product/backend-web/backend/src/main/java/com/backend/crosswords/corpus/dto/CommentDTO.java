package com.backend.crosswords.corpus.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

import java.sql.Timestamp;

@Schema(description = "DTO for getting a comment from all corresponded responses")
public class CommentDTO {
    @Schema(
            example = "1"
    )
    private Long id;
    @Schema(
            example = "Данный текст хорошо характеризует российский футбол 2024 года"
    )
    private String text;
    @Schema(
            example = "2025-03-27T13:04:08.446+00:00"
    )
    @JsonProperty("created_at")
    private Timestamp createdAt;
    @Schema(
            example = "2025-03-27T13:04:08.446+00:00"
    )
    @JsonProperty("updated_at")
    private Timestamp updatedAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public String toString() {
        return "CommentDTO{" +
                "id=" + id +
                ", text='" + text + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
