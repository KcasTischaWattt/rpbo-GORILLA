package com.backend.crosswords.corpus.models;

import java.io.Serializable;
import java.util.Objects;

public class DigestCoreRatingId implements Serializable {
    private Long digestCoreId;
    private Long userId;

    public DigestCoreRatingId(Long digestCoreId, Long userId) {
        this.digestCoreId = digestCoreId;
        this.userId = userId;
    }

    public DigestCoreRatingId() {
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        DigestCoreRatingId that = (DigestCoreRatingId) obj;
        return Objects.equals(digestCoreId, that.digestCoreId) && Objects.equals(userId, that.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(digestCoreId, userId);
    }

    public Long getDigestCoreId() {
        return digestCoreId;
    }

    public void setDigestCoreId(Long digestCoreId) {
        this.digestCoreId = digestCoreId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
