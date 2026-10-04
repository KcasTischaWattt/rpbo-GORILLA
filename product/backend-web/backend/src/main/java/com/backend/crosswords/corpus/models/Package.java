package com.backend.crosswords.corpus.models;

import com.backend.crosswords.admin.models.User;
import jakarta.persistence.*;

import java.util.Set;

@Table(name = "_packages")
@Entity
public class Package {
    @EmbeddedId
    private PackageId id;
    @ManyToOne
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    private User owner;
    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(name = "_docs_packages",
            joinColumns = {
                    @JoinColumn(name = "package_name", referencedColumnName = "name"),
                    @JoinColumn(name = "user_id", referencedColumnName = "user_id")
            },
            inverseJoinColumns = @JoinColumn(name = "doc_id"))
    private Set<DocMeta> docs;

    public static final String favouritesName = "Избранное";

    public Package() {
    }

    public Package(PackageId id, User owner) {
        this.id = id;
        this.owner = owner;
    }

    public PackageId getId() {
        return id;
    }

    public void setId(PackageId id) {
        this.id = id;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public Set<DocMeta> getDocs() {
        return docs;
    }

    public void setDocs(Set<DocMeta> docs) {
        this.docs = docs;
    }
}
