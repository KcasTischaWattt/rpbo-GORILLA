package com.backend.crosswords.corpus.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "DTO for wrapping packages for a document")
public class FoldersForDocDTO {
    private List<PackagesForDocDTO> folders;

    public FoldersForDocDTO() {
    }

    public FoldersForDocDTO(List<PackagesForDocDTO> folders) {
        this.folders = folders;
    }

    public List<PackagesForDocDTO> getFolders() {
        return folders;
    }

    public void setFolders(List<PackagesForDocDTO> folders) {
        this.folders = folders;
    }

    @Override
    public String toString() {
        return "FoldersForDocDTO{" +
                "folders=" + folders +
                '}';
    }
}
