package com.backend.crosswords.corpus.repositories.jpa;

import com.backend.crosswords.corpus.models.DigestTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface DigestTemplateRepository extends JpaRepository<DigestTemplate, String> {
    @Query("SELECT t FROM DigestTemplate t LEFT JOIN FETCH t.sources LEFT JOIN FETCH t.tags WHERE t.uuid = :uuid")
    Optional<DigestTemplate> findFullTemplateById(@Param("uuid") String uuid);
}
