package com.backend.crosswords.corpus.models;

import com.backend.crosswords.admin.models.User;
import jakarta.persistence.*;

@Table(name = "_ratings")
@Entity
public class DocRating {
    @EmbeddedId
    private DocRatingId id;

    @Column(name = "summary_rating")
    private Integer summaryRating;

    @Column(name = "classification_rating")
    private Integer classificationRating;

    @ManyToOne
    @MapsId("docId")
    @JoinColumn(name = "doc_id")
    private DocMeta doc;

    @ManyToOne
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    private User user;

    public DocRating(DocRatingId id, DocMeta doc, User user, Integer summaryRating, Integer classificationRating) {
        this.id = id;
        this.doc = doc;
        this.user = user;
        this.summaryRating = summaryRating;
        this.classificationRating = classificationRating;
    }

    public DocRating() {
    }

    public DocRatingId getId() {
        return id;
    }

    public void setId(DocRatingId id) {
        this.id = id;
    }

    public DocMeta getDoc() {
        return doc;
    }

    public void setDoc(DocMeta doc) {
        this.doc = doc;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Integer getSummaryRating() {
        return summaryRating;
    }

    public void setSummaryRating(Integer summaryRating) {
        this.summaryRating = summaryRating;
    }

    public Integer getClassificationRating() {
        return classificationRating;
    }

    public void setClassificationRating(Integer classificationRating) {
        this.classificationRating = classificationRating;
    }
}
