package com.backend.crosswords.corpus.models;

import jakarta.persistence.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Table(name = "_digests", indexes = {
        @Index(
                name = "idx_digest_subscription_date",
                columnList = "digest_subscription_id, digest_core_id DESC"
        )
})
@Entity
public class Digest {
    @EmbeddedId
    private DigestId id;
    @ManyToOne
    @MapsId("coreId")
    @JoinColumn(name = "digest_core_id")
    private DigestCore core;
    @ManyToOne
    @MapsId("subscriptionId")
    @JoinColumn(name = "digest_subscription_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private DigestSubscription subscription;

    public Digest() {
    }

    public Digest(DigestId id, DigestCore core, DigestSubscription subscription) {
        this.id = id;
        this.core = core;
        this.subscription = subscription;
    }

    public DigestId getId() {
        return id;
    }

    public void setId(DigestId id) {
        this.id = id;
    }

    public DigestCore getCore() {
        return core;
    }

    public void setCore(DigestCore core) {
        this.core = core;
    }

    public DigestSubscription getSubscription() {
        return subscription;
    }

    public void setSubscription(DigestSubscription subscription) {
        this.subscription = subscription;
    }
}
