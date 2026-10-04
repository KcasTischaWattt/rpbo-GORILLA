package com.backend.crosswords.corpus.dto;

import java.util.ArrayList;
import java.util.List;

public class MostRatedDigestSubscriptionsDTO {
    private List<MostRatedDigestSubscriptionDTO> subscriptions;

    public MostRatedDigestSubscriptionsDTO() {
        subscriptions = new ArrayList<>();
    }

    public MostRatedDigestSubscriptionsDTO(List<MostRatedDigestSubscriptionDTO> subscriptions) {
        this.subscriptions = subscriptions;
    }

    public List<MostRatedDigestSubscriptionDTO> getSubscriptions() {
        return subscriptions;
    }

    public void setSubscriptions(List<MostRatedDigestSubscriptionDTO> subscriptions) {
        this.subscriptions = subscriptions;
    }

    @Override
    public String toString() {
        return "MostRatedSubscriptionsDTO{" +
                "subscriptions=" + subscriptions +
                '}';
    }
}
