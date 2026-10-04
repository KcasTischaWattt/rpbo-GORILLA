package com.backend.crosswords.corpus.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "DTO for getting an annotation from doc")
public class AnnotationDTO {
    @Schema(
            example = "1"
    )
    private Long id;
    @Schema(
            description = "the starting position of the annotation in all document",
            example = "0"
    )
    @JsonProperty("start")
    private Integer startPos;
    @Schema(
            description = "the ending position of the annotation in all document",
            example = "10"
    )
    @JsonProperty("end")
    private Integer endPos;
    @Schema(
            example = "['Данный момент явно плохо сформирован', 'стоит уведомить команду о данной ошибке']"
    )
    private List<String> comments;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getStartPos() {
        return startPos;
    }

    public void setStartPos(Integer startPos) {
        this.startPos = startPos;
    }

    public Integer getEndPos() {
        return endPos;
    }

    public void setEndPos(Integer endPos) {
        this.endPos = endPos;
    }

    public List<String> getComments() {
        return comments;
    }

    public void setComments(List<String> comments) {
        this.comments = comments;
    }

    @Override
    public String toString() {
        return "AnnotationDTO{" +
                "id=" + id +
                ", startPos=" + startPos +
                ", endPos=" + endPos +
                ", comments=" + comments +
                '}';
    }
}
