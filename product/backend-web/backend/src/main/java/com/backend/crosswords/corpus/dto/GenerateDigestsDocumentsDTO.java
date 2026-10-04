package com.backend.crosswords.corpus.dto;

public class GenerateDigestsDocumentsDTO {
    private String text;
    private String summary;

    public GenerateDigestsDocumentsDTO() {
    }

    public GenerateDigestsDocumentsDTO(String text, String summary) {
        this.text = text;
        this.summary = summary;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }
}
