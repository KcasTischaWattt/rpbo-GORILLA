package com.backend.crosswords.corpus.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "DTO for creating a digest's subscription")
public class CreateDigestSubscriptionDTO {
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
    private SetSubscribeOptionsDTO subscribeOptions;

    @JsonProperty("public")
    @Schema(description = "Indicates if the digest is public", example = "true")
    private Boolean isPublic;

    @ArraySchema(schema = @Schema(description = "List of followers of the digest", example = "admin"))
    private List<String> followers;

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

    public SetSubscribeOptionsDTO getSubscribeOptions() {
        return subscribeOptions;
    }

    public void setSubscribeOptions(SetSubscribeOptionsDTO subscribeOptions) {
        this.subscribeOptions = subscribeOptions;
    }

    public Boolean getIsPublic() {
        return isPublic;
    }

    public void setIsPublic(Boolean aPublic) {
        isPublic = aPublic;
    }

    public List<String> getFollowers() {
        return followers;
    }

    public void setFollowers(List<String> followers) {
        this.followers = followers;
    }
}
