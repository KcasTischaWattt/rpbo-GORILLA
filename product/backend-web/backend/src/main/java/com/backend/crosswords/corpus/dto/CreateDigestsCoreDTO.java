package com.backend.crosswords.corpus.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "DTO for creating a new digest")
public class CreateDigestsCoreDTO {
    private String text;
    private List<String> sources;
    private List<String> urls;
}
