package com.backend.crosswords.corpus.models;

import java.io.Serializable;
import java.util.Objects;

public class DigestId implements Serializable {
    private Long coreId;
    private Long subscriptionId;

    public DigestId(Long coreId, Long subscriptionId) {
        this.coreId = coreId;
        this.subscriptionId = subscriptionId;
    }

    public DigestId() {
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        DigestId that = (DigestId) obj;
        return Objects.equals(coreId, that.coreId) && Objects.equals(subscriptionId, that.subscriptionId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(coreId, subscriptionId);
    }

    public Long getCoreId() {
        return coreId;
    }

    public void setCoreId(Long coreId) {
        this.coreId = coreId;
    }

    public Long getSubscriptionId() {
        return subscriptionId;
    }

    public void setSubscriptionId(Long subscriptionId) {
        this.subscriptionId = subscriptionId;
    }

    @Override
    public String toString() {
        return coreId + "-" + subscriptionId;
    }
}
