package com.backend.crosswords.corpus.models;

import java.io.Serializable;
import java.util.Objects;

public class PackageId implements Serializable {
    private String name;
    private Long userId;

    public PackageId(String name, Long userId) {
        this.name = name;
        this.userId = userId;
    }

    public PackageId() {
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        PackageId that = (PackageId) obj;
        return Objects.equals(name, that.name) && Objects.equals(userId, that.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, userId);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
