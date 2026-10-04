package com.backend.crosswords.corpus.models;

import com.backend.crosswords.admin.models.User;
import jakarta.persistence.*;

import java.util.List;

@Table(name = "_annotations")
@Entity
public class Annotation {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "start_pos")
    private Integer startPos;
    @Column(name = "end_pos")
    private Integer endPos;
    @ElementCollection
    @CollectionTable(name = "_annotations_comments", joinColumns = @JoinColumn(name = "annotation_id"))
    @Column(name = "comment")
    private List<String> comments;
    @ManyToOne
    @JoinColumn(name = "doc_id")
    private DocMeta doc;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User owner;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<String> getComments() {
        return comments;
    }

    public void setComments(List<String> comments) {
        this.comments = comments;
    }

    public DocMeta getDoc() {
        return doc;
    }

    public void setDoc(DocMeta doc) {
        this.doc = doc;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public Integer getStartPos() {
        return startPos;
    }

    public void setStartPos(Integer startPos) {
        this.startPos = startPos;
    }

    public Integer getEndPos() {
        return endPos;
    }

    public void setEndPos(Integer endPos) {
        this.endPos = endPos;
    }
}
