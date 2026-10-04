package com.backend.crosswords.corpus.models;

import jakarta.persistence.*;

import java.util.List;

@Table(name = "_tags")
@Entity
public class Tag {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(name = "_docs_tags",
            joinColumns = @JoinColumn(name = "tag_id"),
            inverseJoinColumns = @JoinColumn(name = "doc_id"))
    private List<DocMeta> docs;
    @ManyToMany(mappedBy = "tags")
    private List<DigestTemplate> digestTemplates;

    public Tag() {
    }

    public Tag(Long id, String name, List<DocMeta> docs) {
        this.id = id;
        this.name = name;
        this.docs = docs;
    }

    public Tag(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<DocMeta> getDocs() {
        return docs;
    }

    public void setDocs(List<DocMeta> docs) {
        this.docs = docs;
    }

    public List<DigestTemplate> getDigestTemplates() {
        return digestTemplates;
    }

    public void setDigestTemplates(List<DigestTemplate> digestTemplates) {
        this.digestTemplates = digestTemplates;
    }
}
