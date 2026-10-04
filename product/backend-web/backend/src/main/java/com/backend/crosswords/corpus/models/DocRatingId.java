package com.backend.crosswords.corpus.models;

import java.io.Serializable;
import java.util.Objects;

public class DocRatingId implements Serializable {
    private Long docId;
    private Long userId;

    public DocRatingId(Long docId, Long userId) {
        this.docId = docId;
        this.userId = userId;
    }

    public DocRatingId() {
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        DocRatingId that = (DocRatingId) obj;
        return Objects.equals(docId, that.docId) && Objects.equals(userId, that.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(docId, userId);
    }

    public Long getDocId() {
        return docId;
    }

    public void setDocId(Long docId) {
        this.docId = docId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
