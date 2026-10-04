package com.backend.crosswords.corpus.models;

import com.backend.crosswords.admin.models.User;
import jakarta.persistence.*;

import java.sql.Timestamp;
import java.util.List;

@Table(name = "_digest_subscriptions")
@Entity
public class DigestSubscription {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "description")
    private String description;
    @Column(name = "public")
    private Boolean isPublic;
    @Column(name = "send_to_mail")
    private Boolean sendToMail;
    @Column(name = "mobile_notifications")
    private Boolean mobileNotifications;
    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "owner_id")
    private User owner;
    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "template_id")
    private DigestTemplate template;

    @Column(name = "created_at")
    private Timestamp createdAt;
    @OneToMany(mappedBy = "subscription")
    private List<Digest> digests;
    @OneToMany(mappedBy = "digestSubscription")
    private List<DigestSubscriptionSettings> subscriptionSettings;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getIsPublic() {
        return isPublic;
    }

    public void setIsPublic(Boolean isPublic) {
        this.isPublic = isPublic;
        System.out.println(this.isPublic);
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

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public DigestTemplate getTemplate() {
        return template;
    }

    public void setTemplate(DigestTemplate template) {
        this.template = template;
    }

    public List<Digest> getDigests() {
        return digests;
    }

    public void setDigests(List<Digest> digests) {
        this.digests = digests;
    }

    public List<DigestSubscriptionSettings> getSubscriptionSettings() {
        return subscriptionSettings;
    }

    public void setSubscriptionSettings(List<DigestSubscriptionSettings> subscriptionSettings) {
        this.subscriptionSettings = subscriptionSettings;
    }
}
