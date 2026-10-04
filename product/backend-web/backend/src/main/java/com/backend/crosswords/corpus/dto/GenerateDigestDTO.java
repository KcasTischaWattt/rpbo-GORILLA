package com.backend.crosswords.corpus.dto;

import java.util.ArrayList;
import java.util.List;

public class GenerateDigestDTO {
    private List<GenerateDigestsDocumentsDTO> documents;

    public GenerateDigestDTO() {
        documents = new ArrayList<>();
    }

    public GenerateDigestDTO(List<GenerateDigestsDocumentsDTO> documents) {
        this.documents = documents;
    }

    public List<GenerateDigestsDocumentsDTO> getDocuments() {
        return documents;
    }

    public void setDocuments(List<GenerateDigestsDocumentsDTO> documents) {
        this.documents = documents;
    }
}
