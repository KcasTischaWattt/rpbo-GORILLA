package com.backend.crosswords.corpus.models;

import com.backend.crosswords.corpus.enums.Language;
import com.backend.crosswords.corpus.enums.Source;
import jakarta.persistence.*;

import java.sql.Timestamp;
import java.util.List;
import java.util.Set;

@Table(name = "_documents")
@Entity
public class DocMeta {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "summary", columnDefinition = "TEXT")
    private String summary;

    @Column(name = "date")
    private Timestamp date;

    @Column(name = "last_edit")
    private Timestamp lastEdit;

    @Column(name = "url")
    private String url;

    @Column(name = "language")
    @Enumerated(EnumType.STRING)
    private Language language;

    @Column(name = "source")
    @Enumerated(EnumType.STRING)
    private Source source;

    @ManyToMany(mappedBy = "docs")
    private List<Tag> tags;

    @ManyToMany(mappedBy = "docs")
    private Set<Package> packages;

    @OneToMany(mappedBy = "doc", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DocRating> ratings;

    @OneToMany(mappedBy = "doc", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Annotation> annotations;

    @OneToMany(mappedBy = "doc", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments;

    @ManyToMany
    @JoinTable(name = "_digest_cores_docs",
            joinColumns = @JoinColumn(name = "doc_id"),
            inverseJoinColumns = @JoinColumn(name = "digest_core_id"))
    private List<DigestCore> digestCores;

    // TODO выгрузка дайджестов в PDF

    public DocMeta() {
    }

    public DocMeta(Long id, String summary, Timestamp date, Timestamp lastEdit, String url, Language language, Source source, List<Tag> tags, List<DocRating> ratings) {
        this.id = id;
        this.summary = summary;
        this.date = date;
        this.lastEdit = lastEdit;
        this.url = url;
        this.language = language;
        this.source = source;
        this.tags = tags;
        this.ratings = ratings;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public Timestamp getDate() {
        return date;
    }

    public void setDate(Timestamp date) {
        this.date = date;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }


    public List<Tag> getTags() {
        return tags;
    }

    public void setTags(List<Tag> tags) {
        this.tags = tags;
    }

    public List<DocRating> getRatings() {
        return ratings;
    }

    public void setRatings(List<DocRating> ratings) {
        this.ratings = ratings;
    }

    public Language getLanguage() {
        return language;
    }

    public void setLanguage(Language language) {
        this.language = language;
    }

    public Source getSource() {
        return source;
    }

    public void setSource(Source source) {
        this.source = source;
    }

    public Timestamp getLastEdit() {
        return lastEdit;
    }

    public void setLastEdit(Timestamp lastEdit) {
        this.lastEdit = lastEdit;
    }

    public Set<Package> getPackages() {
        return packages;
    }

    public void setPackages(Set<Package> packages) {
        this.packages = packages;
    }

    public List<DigestCore> getDigestCores() {
        return digestCores;
    }

    public void setDigestCores(List<DigestCore> digestCores) {
        this.digestCores = digestCores;
    }

    public List<Annotation> getAnnotations() {
        return annotations;
    }

    public void setAnnotations(List<Annotation> annotations) {
        this.annotations = annotations;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }
}
