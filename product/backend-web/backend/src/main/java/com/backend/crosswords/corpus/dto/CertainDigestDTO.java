package com.backend.crosswords.corpus.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Schema(description = "DTO for getting info about a certain digest by its id")
public class CertainDigestDTO {
    @Schema(example = "1#1")
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
    @JsonProperty("subscribe_options")
    private GetSubscribeOptionsDTO subscribeOptions;

    @JsonProperty("based_on")
    private List<BasedOnDTO> basedOn;

    @Schema(example = "true")
    @JsonProperty("is_authed")
    private Boolean isAuthed;

    public CertainDigestDTO() {
        sources = new ArrayList<>();
        tags = new ArrayList<>();
        basedOn = new ArrayList<>();
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

    public List<BasedOnDTO> getBasedOn() {
        return basedOn;
    }

    public void setBasedOn(List<BasedOnDTO> basedOn) {
        this.basedOn = basedOn;
    }

    public Boolean getIsAuthed() {
        return isAuthed;
    }

    public void setIsAuthed(Boolean authed) {
        isAuthed = authed;
    }

    @Override
    public String toString() {
        return "GetDigestByIdDTO{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", averageRating=" + averageRating +
                ", userRating=" + userRating +
                ", sources=" + sources +
                ", description='" + description + '\'' +
                ", text='" + text + '\'' +
                ", tags=" + tags +
                ", date=" + date +
                ", isPublic=" + isPublic +
                ", isOwner=" + isOwner +
                ", owner='" + owner + '\'' +
                ", subscribeOptions=" + subscribeOptions +
                ", basedOn=" + basedOn +
                ", isAuthed=" + isAuthed +
                '}';
    }
}
