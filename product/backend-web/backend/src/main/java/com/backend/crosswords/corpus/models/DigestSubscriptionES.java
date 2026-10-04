package com.backend.crosswords.corpus.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Document(indexName = "digest_subscription")
public class DigestSubscriptionES {
    @Id
    private Long id;
    @Field(type = FieldType.Text, name = "title")
    private String title;

    public DigestSubscriptionES() {
    }

    public DigestSubscriptionES(Long id, String title) {
        this.id = id;
        this.title = title;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
