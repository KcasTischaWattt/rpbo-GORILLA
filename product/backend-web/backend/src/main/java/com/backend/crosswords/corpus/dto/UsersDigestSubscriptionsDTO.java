package com.backend.crosswords.corpus.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

public class UsersDigestSubscriptionsDTO {
    @JsonProperty("digest_subscriptions")
    private List<UsersDigestSubscriptionDTO> usersDigestSubscriptions;

    public UsersDigestSubscriptionsDTO() {
        usersDigestSubscriptions = new ArrayList<>();
    }

    public List<UsersDigestSubscriptionDTO> getUsersDigestSubscriptions() {
        return usersDigestSubscriptions;
    }

    public void setUsersDigestSubscriptions(List<UsersDigestSubscriptionDTO> usersDigestSubscriptions) {
        this.usersDigestSubscriptions = usersDigestSubscriptions;
    }

    @Override
    public String toString() {
        return "UsersDigestSubscriptionsDTO{" +
                "usersDigestSubscriptions=" + usersDigestSubscriptions +
                '}';
    }
}
