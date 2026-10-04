package com.backend.crosswords.corpus.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "DTO to wrap DocDTO for a corresponding requests")
public class DocsDTO {
    private List<DocDTO> docs;

    public DocsDTO() {
    }

    public DocsDTO(List<DocDTO> docs) {
        this.docs = docs;
    }

    public List<DocDTO> getDocs() {
        return docs;
    }

    public void setDocs(List<DocDTO> docs) {
        this.docs = docs;
    }

    @Override
    public String toString() {
        return "DocsDTO{" +
                "docs=" + docs +
                '}';
    }
}
