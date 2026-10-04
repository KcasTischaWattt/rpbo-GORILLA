package com.backend.crosswords.corpus.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "DTO for getting a comment from all corresponded responses")
public class AnnotationsIdDTO {
    @Schema(example = "1")
    private Long id;

    public AnnotationsIdDTO() {
    }

    public AnnotationsIdDTO(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "AnnotationsIdDTO{" +
                "id=" + id +
                '}';
    }
}
