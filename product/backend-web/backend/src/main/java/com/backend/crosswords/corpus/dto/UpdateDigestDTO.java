package com.backend.crosswords.corpus.dto;

public class UpdateDigestDTO {
    private String text;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return "UpdateDigestDTO{" +
                "text='" + text + '\'' +
                '}';
    }
}
