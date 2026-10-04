package com.backend.crosswords.corpus.models;

import com.backend.crosswords.admin.models.User;
import jakarta.persistence.*;

@Table(name = "_digest_core_ratings")
@Entity
public class DigestCoreRating {
    @EmbeddedId
    private DigestCoreRatingId id;
    @Column(name = "digest_rating")
    private Integer digestCoreRating;
    @ManyToOne
    @MapsId("digestCoreId")
    @JoinColumn(name = "digest_core_id")
    private DigestCore core;
    @ManyToOne
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    private User user;
    public DigestCoreRating(DigestCoreRatingId id, DigestCore core, User user, Integer digestRating) {
        this.id = id;
        this.core = core;
        this.user = user;
        this.digestCoreRating = digestRating;
    }

    public DigestCoreRating() {
    }

    public DigestCoreRatingId getId() {
        return id;
    }

    public void setId(DigestCoreRatingId id) {
        this.id = id;
    }

    public DigestCore getCore() {
        return core;
    }

    public void setCore(DigestCore core) {
        this.core = core;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Integer getDigestCoreRating() {
        return digestCoreRating;
    }

    public void setDigestCoreRating(Integer digestRating) {
        this.digestCoreRating = digestRating;
    }
}
