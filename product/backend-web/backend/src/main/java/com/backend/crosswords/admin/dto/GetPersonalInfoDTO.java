package com.backend.crosswords.admin.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

public class GetPersonalInfoDTO {
    @JsonProperty("first_name")
    private String firstName;
    @JsonProperty("second_name")
    private String secondName;
    private String username;
    private String email;
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

    @JsonProperty("telegram")
    private Boolean isTelegram;

    public GetPersonalInfoDTO() {
    }

    public GetPersonalInfoDTO(String firstName, String secondName, String username, String email, Boolean sendToMail, Boolean mobileNotifications, Boolean personalSendToMail, Boolean personalMobileNotifications, Boolean subscribable, Boolean isTelegram) {
        this.firstName = firstName;
        this.secondName = secondName;
        this.username = username;
        this.email = email;
        this.sendToMail = sendToMail;
        this.mobileNotifications = mobileNotifications;
        this.personalSendToMail = personalSendToMail;
        this.personalMobileNotifications = personalMobileNotifications;
        this.subscribable = subscribable;
        this.isTelegram = isTelegram;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getSecondName() {
        return secondName;
    }

    public void setSecondName(String secondName) {
        this.secondName = secondName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

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

    public Boolean getTelegram() {
        return isTelegram;
    }

    public void setTelegram(Boolean telegram) {
        isTelegram = telegram;
    }

    @Override
    public String toString() {
        return "GetPersonalInfoDTO{" +
                "firstName='" + firstName + '\'' +
                ", secondName='" + secondName + '\'' +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", sendToMail=" + sendToMail +
                ", mobileNotifications=" + mobileNotifications +
                ", personalSendToMail=" + personalSendToMail +
                ", personalMobileNotifications=" + personalMobileNotifications +
                ", subscribable=" + subscribable +
                ", isTelegram=" + isTelegram +
                '}';
    }
}
