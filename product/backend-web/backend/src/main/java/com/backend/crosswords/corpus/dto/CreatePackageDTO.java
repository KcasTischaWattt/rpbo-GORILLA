package com.backend.crosswords.corpus.dto;

import com.backend.crosswords.admin.models.CrosswordUserDetails;
import com.backend.crosswords.corpus.models.PackageId;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@Schema(description = "DTO for creating a package")
public class CreatePackageDTO {
    @Schema(
            example = "Футбол 2024",
            description = "The name of the package, remember, that name for each user is unique and must be less then 256 characters long"
    )
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "CreatePackageDTO{" +
                "name='" + name + '\'' +
                '}';
    }
}
