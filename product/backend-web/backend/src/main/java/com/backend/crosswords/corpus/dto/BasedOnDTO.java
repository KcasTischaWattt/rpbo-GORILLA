package com.backend.crosswords.corpus.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "DTO for getting info about documents on which the digest is based on")
public class BasedOnDTO {
    @Schema(example = "1")
    private Long id;
    @Schema(example = "Чемпион мира WBA Гаджимагомедов победил Мчуну из ЮАР")
    private String title;
    @Schema(example = "https://www.kommersant.ru/doc/7566629")
    private String url;

    public BasedOnDTO() {
    }

    public BasedOnDTO(Long id, String title, String url) {
        this.id = id;
        this.title = title;
        this.url = url;
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

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return "BasedOnDTO{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", url='" + url + '\'' +
                '}';
    }
}
