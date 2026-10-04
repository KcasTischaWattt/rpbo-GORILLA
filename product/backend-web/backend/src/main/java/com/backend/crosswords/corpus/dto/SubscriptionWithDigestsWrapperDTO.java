package com.backend.crosswords.corpus.dto;

public class SubscriptionWithDigestsWrapperDTO {
    private SubscriptionWithDigestsDTO subscription;

    public SubscriptionWithDigestsWrapperDTO() {
    }

    public SubscriptionWithDigestsWrapperDTO(SubscriptionWithDigestsDTO subscription) {
        this.subscription = subscription;
    }

    public SubscriptionWithDigestsDTO getSubscription() {
        return subscription;
    }

    public void setSubscription(SubscriptionWithDigestsDTO subscription) {
        this.subscription = subscription;
    }

    @Override
    public String toString() {
        return "SubscriptionWithDigestsWrapperDTO{" +
                "subscription=" + subscription +
                '}';
    }
}
