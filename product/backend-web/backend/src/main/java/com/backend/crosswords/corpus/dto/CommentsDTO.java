package com.backend.crosswords.corpus.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "DTO for getting a list of comments from all corresponded responses")
public class CommentsDTO {
    private List<CommentDTO> comments;

    public List<CommentDTO> getComments() {
        return comments;
    }

    public void setComments(List<CommentDTO> comments) {
        this.comments = comments;
    }

    @Override
    public String toString() {
        return "CommentsDTO{" +
                "comments=" + comments +
                '}';
    }
}
