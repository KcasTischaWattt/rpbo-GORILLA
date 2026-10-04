package com.backend.crosswords.corpus.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "DTO for creating a tag")
public class CreateTagDTO {
    @Schema(example = "Финансы")
    private String name;

    public CreateTagDTO(String name) {
        this.name = name;
    }

    public CreateTagDTO() {
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
