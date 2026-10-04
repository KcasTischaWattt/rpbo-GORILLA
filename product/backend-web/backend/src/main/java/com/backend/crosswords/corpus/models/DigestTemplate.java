package com.backend.crosswords.corpus.models;

import com.backend.crosswords.corpus.enums.Source;
import jakarta.persistence.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Table(name = "_digest_templates")
@Entity
public class DigestTemplate {
    @Id
    @Column(name = "id", unique = true, nullable = false)
    private String uuid;
    @ElementCollection
    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "_digest_templates_sources", joinColumns = @JoinColumn(name = "digest_template_id"))
    private Set<Source> sources = new HashSet<>();
    @ManyToMany(cascade = {CascadeType.MERGE})
    @JoinTable(name = "_digest_templates_tags",
            joinColumns = @JoinColumn(name = "digest_template_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id"))
    private Set<Tag> tags;
    @OneToMany(mappedBy = "template", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private List<DigestCore> cores;

    public DigestTemplate(Set<Source> sources, Set<Tag> tags) {
        this.sources = sources;
        this.tags = tags;
    }

    public DigestTemplate() {
    }

    @PrePersist
    private void generateUuidForDigest() {
        this.uuid = generateUuid(this.sources, this.tags);
    }
    public static String generateUuid(Set<Source> sources, Set<Tag> tags) {
        String sourcesHash = sources.stream()
                .map(Source::ordinal)
                .sorted()
                .map(Object::toString)
                .collect(Collectors.joining("-"));

        String tagsHash = tags.stream()
                .map(Tag::getId)
                .sorted()
                .map(Object::toString)
                .collect(Collectors.joining("-"));
        return sourcesHash + "#" + tagsHash;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public Set<Source> getSources() {
        return sources;
    }

    public void setSources(Set<Source> sources) {
        this.sources = sources;
    }

    public Set<Tag> getTags() {
        return tags;
    }

    public void setTags(Set<Tag> tags) {
        this.tags = tags;
    }

    public List<DigestCore> getCores() {
        return cores;
    }

    public void setCores(List<DigestCore> cores) {
        this.cores = cores;
    }
}
