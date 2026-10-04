package com.backend.crosswords.corpus.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

public class SendDigestByEmailsDTO {
    private List<String> recipients;
    private String title;
    private String text;
    @JsonProperty("web_link")
    private String webLink;

    public SendDigestByEmailsDTO() {
        recipients = new ArrayList<>();
    }

    public SendDigestByEmailsDTO(List<String> recipients, String title, String text, String webLink) {
        this.recipients = recipients;
        this.title = title;
        this.text = text;
        this.webLink = webLink;
    }

    public List<String> getRecipients() {
        return recipients;
    }

    public void setRecipients(List<String> recipients) {
        this.recipients = recipients;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getWebLink() {
        return webLink;
    }

    public void setWebLink(String webLink) {
        this.webLink = webLink;
    }
}
