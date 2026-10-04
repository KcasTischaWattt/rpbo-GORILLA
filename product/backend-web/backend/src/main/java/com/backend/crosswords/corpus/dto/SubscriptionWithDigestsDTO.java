package com.backend.crosswords.corpus.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

import java.sql.Timestamp;
import java.util.List;

public class SubscriptionWithDigestsDTO {
    @Schema(example = "1")
    @JsonProperty("next_page")
    private Long nextPage;
    @Schema(example = "admin")
    private String owner;
    @Schema(example = "1")
    private Long id;
    @Schema(example = "Нечто страшное")
    private String description;
    @Schema(example = "Нечто страшное")
    private String title;
    @Schema(example = "4.6")
    @JsonProperty("average_rating")
    private Double averageRating;
    @Schema(example = "05/03/2025")
    @JsonFormat(pattern = "dd/MM/yyyy")
    @JsonProperty("creation_date")
    private Timestamp date;
    @JsonProperty("is_authed")
    private Boolean isAuthed;
    @Schema(example = "true")
    @JsonProperty("public")
    private Boolean isPublic;
    @Schema(example = "true")
    @JsonProperty("is_owner")
    private Boolean isOwner;
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
    @JsonProperty("subscribe_options")
    private GetSubscribeOptionsDTO subscribeOptions;
    private List<SubscriptionsDigestDTO> digests;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(Double averageRating) {
        this.averageRating = averageRating;
    }

    public Timestamp getDate() {
        return date;
    }

    public void setDate(Timestamp date) {
        this.date = date;
    }

    public Boolean getIsAuthed() {
        return isAuthed;
    }

    public void setIsAuthed(Boolean isAuthed) {
        this.isAuthed = isAuthed;
    }

    public Boolean getIsPublic() {
        return isPublic;
    }

    public void setIsPublic(Boolean isPublic) {
        this.isPublic = isPublic;
    }

    public Boolean getIsOwner() {
        return isOwner;
    }

    public void setIsOwner(Boolean isOwner) {
        this.isOwner = isOwner;
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

    public GetSubscribeOptionsDTO getSubscribeOptions() {
        return subscribeOptions;
    }

    public void setSubscribeOptions(GetSubscribeOptionsDTO subscribeOptions) {
        this.subscribeOptions = subscribeOptions;
    }

    public List<SubscriptionsDigestDTO> getDigests() {
        return digests;
    }

    public void setDigests(List<SubscriptionsDigestDTO> digests) {
        this.digests = digests;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public Long getNextPage() {
        return nextPage;
    }

    public void setNextPage(Long nextPage) {
        this.nextPage = nextPage;
    }

    @Override
    public String toString() {
        return "SubscriptionWithDigestsDTO{" +
                "nextPage=" + nextPage +
                ", owner='" + owner + '\'' +
                ", id=" + id +
                ", description='" + description + '\'' +
                ", title='" + title + '\'' +
                ", averageRating=" + averageRating +
                ", date=" + date +
                ", isAuthed=" + isAuthed +
                ", isPublic=" + isPublic +
                ", isOwner=" + isOwner +
                ", sources=" + sources +
                ", tags=" + tags +
                ", subscribeOptions=" + subscribeOptions +
                ", digests=" + digests +
                '}';
    }
}
