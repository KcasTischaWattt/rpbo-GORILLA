package com.backend.crosswords.corpus.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class MostRatedDigestSubscriptionDTO {
    private Long id;
    private String title;
    private String description;
    @Schema(example = "05/03/2025")
    @JsonFormat(pattern = "dd/MM/yyyy")
    private Timestamp date;
    @Schema(example = "[\n" +
            "            \"Спорт\",\n" +
            "            \"РБК\"\n" +
            "        ]")
    private List<String> sources;
    @Schema(example = "[\n" +
            "            \"Спорт\",\n" +
            "            \"Финансы\"\n" +
            "        ]")
    private List<String> tags;

    public MostRatedDigestSubscriptionDTO() {
        sources = new ArrayList<>();
        tags = new ArrayList<>();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Timestamp getDate() {
        return date;
    }

    public void setDate(Timestamp date) {
        this.date = date;
    }

    public List<String> getSources() {
        return sources;
    }

    public void setSources(List<String> sources) {
        this.sources = sources;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    @Override
    public String toString() {
        return "MostRatedSubscriptionDTO{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", date=" + date +
                ", sources=" + sources +
                ", tags=" + tags +
                '}';
    }
}
