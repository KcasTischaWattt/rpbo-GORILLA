package com.backend.crosswords.corpus.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

import java.sql.Timestamp;

@Schema(description = "DTO for creating or updating a comment")
public class CreateUpdateCommentDTO {
    @Schema(example = "Данный текст хорошо характеризует российский футбол 2024 года")
    private String text;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }


    @Override
    public String toString() {
        return "CreateUpdateCommentDTO{" +
                "text='" + text + '\'' +
                '}';
    }
}
