package com.backend.crosswords.corpus.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Schema(description = "DTO to get digest")
public class DigestDTO {
    @Schema(example = "1-1")
    private String id;
    @Schema(example = "Нечто страшное")
    private String title;
    @Schema(example = "4.6")
    @JsonProperty("average_rating")
    private Double averageRating;
    @Schema(example = "5")
    @JsonProperty("user_rating")
    private Integer userRating;
    @Schema(example = "[\n" +
            "            \"Спорт\",\n" +
            "            \"РБК\"\n" +
            "        ]")
    private List<String> sources;
    @Schema(example = "Нечто страшное")
    private String description;
    @Schema(example = "Нечто страшное")
    private String text;
    @Schema(example = "[\n" +
            "            \"Спорт\",\n" +
            "            \"Финансы\"\n" +
            "        ]")
    private List<String> tags;
    @Schema(example = "05/03/2025")
    @JsonFormat(pattern = "dd/MM/yyyy")
    private Timestamp date;
    @Schema(example = "true")
    @JsonProperty("public")
    private Boolean isPublic;
    @Schema(example = "true")
    @JsonProperty("is_owner")
    private Boolean isOwner;
    @Schema(example = "admin")
    private String owner;
    @Schema(example = "[\n" +
            "            \"https://www.interfax.ru/russia/1020840\",\n" +
            "            \"https://www.interfax.ru/photo/6996\"\n" +
            "        ]")
    private List<String> urls;
    @JsonProperty("subscribe_options")
    private GetSubscribeOptionsDTO subscribeOptions;

    public DigestDTO() {
        sources = new ArrayList<>();
        tags = new ArrayList<>();
        urls = new ArrayList<>();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Double getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(Double averageRating) {
        this.averageRating = averageRating;
    }

    public Integer getUserRating() {
        return userRating;
    }

    public void setUserRating(Integer userRating) {
        this.userRating = userRating;
    }

    public List<String> getSources() {
        return sources;
    }

    public void setSources(List<String> sources) {
        this.sources = sources;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public Timestamp getDate() {
        return date;
    }

    public void setDate(Timestamp date) {
        this.date = date;
    }

    public Boolean getIsPublic() {
        return isPublic;
    }

    public void setIsPublic(Boolean aPublic) {
        isPublic = aPublic;
    }

    public Boolean getIsOwner() {
        return isOwner;
    }

    public void setIsOwner(Boolean owner) {
        this.isOwner = owner;
    }

    public List<String> getUrls() {
        return urls;
    }

    public void setUrls(List<String> urls) {
        this.urls = urls;
    }

    public GetSubscribeOptionsDTO getSubscribeOptions() {
        return subscribeOptions;
    }

    public void setSubscribeOptions(GetSubscribeOptionsDTO subscribeOptions) {
        this.subscribeOptions = subscribeOptions;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }
}
