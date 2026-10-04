package com.backend.crosswords.corpus.models;

import com.backend.crosswords.admin.models.User;
import jakarta.persistence.*;

@Table(name = "_digest_subscription_settings")
@Entity
public class DigestSubscriptionSettings {
    @EmbeddedId
    private DigestSubscriptionSettingsId id;
    @Column(name = "send_to_mail")
    private Boolean sendToMail;
    @Column(name = "mobile_Notifications")
    private Boolean mobileNotifications;
    @Column(name = "edited")
    private Boolean edited = false;
    @ManyToOne
    @MapsId("digestSubscriptionId")
    @JoinColumn(name = "digest_subscription_id")
    private DigestSubscription digestSubscription;
    @ManyToOne
    @MapsId("userId")
    @JoinColumn(name = "subscriber_id")
    private User subscriber;

    public DigestSubscriptionSettings(DigestSubscriptionSettingsId id, DigestSubscription digestSubscription, User subscriber, Boolean sendToMail, Boolean mobileNotifications) {
        this.id = id;
        this.digestSubscription = digestSubscription;
        this.subscriber = subscriber;
        this.sendToMail = sendToMail;
        this.mobileNotifications = mobileNotifications;
    }

    public DigestSubscriptionSettings() {
    }

    public DigestSubscriptionSettingsId getId() {
        return id;
    }

    public void setId(DigestSubscriptionSettingsId id) {
        this.id = id;
    }

    public DigestSubscription getDigestSubscription() {
        return digestSubscription;
    }

    public void setDigestSubscription(DigestSubscription digestSubscription) {
        this.digestSubscription = digestSubscription;
    }

    public User getSubscriber() {
        return subscriber;
    }

    public void setSubscriber(User subscriber) {
        this.subscriber = subscriber;
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

    public Boolean getEdited() {
        return edited;
    }

    public void setEdited(Boolean edited) {
        this.edited = edited;
    }
}
