package com.backend.crosswords.corpus.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "DTO for getting tags in corresponding requests for getting documents")
public class TagDTO {
    @Schema(example = "Спорт")
    private String name;

    public TagDTO(String name) {
        this.name = name;
    }

    public TagDTO() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "CreateTagDTO{" +
                "name='" + name + '\'' +
                '}';
    }
}
