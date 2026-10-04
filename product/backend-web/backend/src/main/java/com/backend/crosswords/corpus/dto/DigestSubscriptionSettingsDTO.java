package com.backend.crosswords.corpus.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class DigestSubscriptionSettingsDTO {
    private Boolean subscribed;
    @JsonProperty("send_to_mail")
    private Boolean sendToMail;
    @JsonProperty("mobile_notifications")
    private Boolean mobileNotifications;

    public DigestSubscriptionSettingsDTO() {
    }

    public DigestSubscriptionSettingsDTO(Boolean subscribed, Boolean sendToMail, Boolean mobileNotifications) {
        this.subscribed = subscribed;
        this.sendToMail = sendToMail;
        this.mobileNotifications = mobileNotifications;
    }

    public Boolean getSubscribed() {
        return subscribed;
    }

    public void setSubscribed(Boolean subscribed) {
        this.subscribed = subscribed;
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
}
