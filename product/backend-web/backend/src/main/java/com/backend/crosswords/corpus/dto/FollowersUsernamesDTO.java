package com.backend.crosswords.corpus.dto;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "DTO for wrapping a list of followers' usernames")
public class FollowersUsernamesDTO {
    @ArraySchema(schema = @Schema(description = "List of followers of the digest", example = "admin"))
    private List<String> followersUsernames;

    public List<String> getFollowers() {
        return followersUsernames;
    }

    public void setFollowers(List<String> followersUsernames) {
        this.followersUsernames = followersUsernames;
    }

    @Override
    public String toString() {
        return "FollowersDTO{" +
                "followers=" + followersUsernames +
                '}';
    }
}
