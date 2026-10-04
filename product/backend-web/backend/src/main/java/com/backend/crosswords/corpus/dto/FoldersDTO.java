package com.backend.crosswords.corpus.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "DTO for getting all packages from the user")
public class FoldersDTO {
    @Schema(
            example = """
                    [
                                "Избранное",
                                "Футбол 2024"
                            ]"""
    )
    private List<String> folders;

    public FoldersDTO() {
    }

    public FoldersDTO(List<String> folders) {
        this.folders = folders;
    }

    public List<String> getFolders() {
        return folders;
    }

    public void setFolders(List<String> folders) {
        this.folders = folders;
    }

    @Override
    public String toString() {
        return "FoldersDTO{" +
                "folders=" + folders +
                '}';
    }
}
