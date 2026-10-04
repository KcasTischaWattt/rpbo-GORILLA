package com.backend.crosswords.corpus.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class DigestSubscriptionFollowerDTO {
    private String email;
    @JsonProperty("send_to_mail")
    private Boolean sendToMail;
    @JsonProperty("mobile_notifications")
    private Boolean mobileNotifications;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Boolean getSendToMail() {
        return sendToMail;
    }

    public void setSendToMail(Boolean sendToMail) {
        this.sendToMail = sendToMail;
    }

    public Boolean getMobileNotifications() {
        return mobileNotifications;
    }

    public void setMobileNotifications(Boolean mobileNotifications) {
        this.mobileNotifications = mobileNotifications;
    }

    @Override
    public String toString() {
        return "DigestSubscriptionFollowersDTO{" +
                "email='" + email + '\'' +
                ", sendToMail=" + sendToMail +
                ", mobileNotifications=" + mobileNotifications +
                '}';
    }
}
