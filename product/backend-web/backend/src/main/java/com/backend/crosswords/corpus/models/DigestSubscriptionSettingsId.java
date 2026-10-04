package com.backend.crosswords.corpus.models;

import java.io.Serializable;
import java.util.Objects;

public class DigestSubscriptionSettingsId implements Serializable {
    private Long digestSubscriptionId;
    private Long userId;

    public DigestSubscriptionSettingsId(Long digestSubscriptionId, Long userId) {
        this.digestSubscriptionId = digestSubscriptionId;
        this.userId = userId;
    }

    public DigestSubscriptionSettingsId() {
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        DigestSubscriptionSettingsId that = (DigestSubscriptionSettingsId) obj;
        return Objects.equals(digestSubscriptionId, that.digestSubscriptionId) && Objects.equals(userId, that.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(digestSubscriptionId, userId);
    }

    public Long getDigestSubscriptionId() {
        return digestSubscriptionId;
    }

    public void setDigestSubscriptionId(Long digestSubscriptionId) {
        this.digestSubscriptionId = digestSubscriptionId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
