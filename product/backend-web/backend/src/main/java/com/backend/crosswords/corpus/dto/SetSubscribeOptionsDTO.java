package com.backend.crosswords.corpus.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "DTO for getting subscribe options from requests and put into the corresponding digest's DTOs")
public class SetSubscribeOptionsDTO {
    @JsonProperty("send_to_mail")
    private Boolean sendToMail;
    @JsonProperty("mobile_notifications")
    private Boolean mobileNotifications;

    public SetSubscribeOptionsDTO() {
    }

    public SetSubscribeOptionsDTO(Boolean sendToMail, Boolean mobileNotifications) {
        this.sendToMail = sendToMail;
        this.mobileNotifications = mobileNotifications;
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
