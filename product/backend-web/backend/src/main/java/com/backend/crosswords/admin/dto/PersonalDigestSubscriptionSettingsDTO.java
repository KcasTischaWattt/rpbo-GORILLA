package com.backend.crosswords.admin.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "DTO for user's subscription settings")
public class PersonalDigestSubscriptionSettingsDTO {
    @Schema(
            example = "true"
    )
    @JsonProperty("send_to_mail")
    private Boolean sendToMail;
    @Schema(
            example = "true"
    )
    @JsonProperty("mobile_notifications")
    private Boolean mobileNotifications;
    @Schema(
            example = "true"
    )
    @JsonProperty("personal_send_to_mail")
    private Boolean personalSendToMail;
    @Schema(
            example = "true"
    )
    @JsonProperty("personal_mobile_notifications")
    private Boolean personalMobileNotifications;
    @Schema(
            example = "true"
    )
    private Boolean subscribable;

    public PersonalDigestSubscriptionSettingsDTO() {
    }

    public PersonalDigestSubscriptionSettingsDTO(Boolean sendToMail, Boolean mobileNotifications, Boolean personalSendToMail, Boolean personalMobileNotifications, Boolean subscribable) {
        this.sendToMail = sendToMail;
        this.mobileNotifications = mobileNotifications;
        this.personalSendToMail = personalSendToMail;
        this.personalMobileNotifications = personalMobileNotifications;
        this.subscribable = subscribable;
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

    public Boolean getPersonalSendToMail() {
        return personalSendToMail;
    }

    public void setPersonalSendToMail(Boolean personalSendToMail) {
        this.personalSendToMail = personalSendToMail;
    }

    public Boolean getPersonalMobileNotifications() {
        return personalMobileNotifications;
    }

    public void setPersonalMobileNotifications(Boolean personalMobileNotifications) {
        this.personalMobileNotifications = personalMobileNotifications;
    }

    public Boolean getSubscribable() {
        return subscribable;
    }

    public void setSubscribable(Boolean subscribable) {
        this.subscribable = subscribable;
    }

    @Override
    public String toString() {
        return "PersonalDigestSubscriptionSettingsDTO{" +
                "sendToMail=" + sendToMail +
                ", mobileNotifications=" + mobileNotifications +
                ", personalSendToMail=" + personalSendToMail +
                ", personalMobileNotifications=" + personalMobileNotifications +
                ", subscribable=" + subscribable +
                '}';
    }
}
