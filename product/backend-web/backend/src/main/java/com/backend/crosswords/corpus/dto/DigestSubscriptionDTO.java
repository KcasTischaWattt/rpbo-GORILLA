package com.backend.crosswords.corpus.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class DigestSubscriptionDTO {
    @Schema(example = "1")
    private Long id;

    @Schema(description = "Title of the digest", example = "Главное о спорте")
    private String title;

    @Schema(description = "Description of the digest", example = "самое важно о мире спорта")
    private String description;

    @ArraySchema(schema = @Schema(description = "List of sources for the digest", example = "Интерфакс"))
    private List<String> sources;

    @ArraySchema(schema = @Schema(description = "List of tags associated with the digest", example = "Спорт"))
    private List<String> tags;

    @JsonProperty("subscribe_options")
    @Schema(description = "Subscription options for the digest")
    private GetSubscribeOptionsDTO subscribeOptions;

    @JsonProperty("public")
    @Schema(description = "Indicates if the digest is public", example = "true")
    private Boolean isPublic;

    @Schema(example = "admin")
    @JsonProperty("owner")
    private String ownersUsername;
    @JsonProperty("is_owner")
    private Boolean isOwner;
    @JsonProperty("creation_date")
    @JsonFormat(pattern = "dd/MM/yyyy")
    private Timestamp creationDate;

    private List<DigestSubscriptionFollowerDTO> followers;

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

    public Boolean getPublic() {
        return isPublic;
    }

    public void setPublic(Boolean aPublic) {
        isPublic = aPublic;
    }

    public String getOwnersUsername() {
        return ownersUsername;
    }

    public void setOwnersUsername(String ownersUsername) {
        this.ownersUsername = ownersUsername;
    }

    public List<DigestSubscriptionFollowerDTO> getFollowers() {
        return followers;
    }

    public void setFollowers(List<DigestSubscriptionFollowerDTO> followers) {
        this.followers = followers;
    }

    public Boolean getIsOwner() {
        return isOwner;
    }

    public void setIsOwner(Boolean isOwner) {
        this.isOwner = isOwner;
    }

    public Timestamp getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Timestamp creationDate) {
        this.creationDate = creationDate;
    }

    @Override
    public String toString() {
        return "DigestSubscriptionDTO{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", sources=" + sources +
                ", tags=" + tags +
                ", subscribeOptions=" + subscribeOptions +
                ", isPublic=" + isPublic +
                ", ownersUsername='" + ownersUsername + '\'' +
                ", isOwner=" + isOwner +
                ", creationDate=" + creationDate +
                ", followers=" + followers +
                '}';
    }
}
