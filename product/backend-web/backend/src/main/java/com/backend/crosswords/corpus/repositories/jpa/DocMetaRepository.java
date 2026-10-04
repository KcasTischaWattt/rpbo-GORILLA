package com.backend.crosswords.corpus.repositories.jpa;

import com.backend.crosswords.admin.models.User;
import com.backend.crosswords.corpus.enums.Language;
import com.backend.crosswords.corpus.enums.Source;
import com.backend.crosswords.corpus.models.DocMeta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.sql.Timestamp;
import java.util.List;
import java.util.Set;

public interface DocMetaRepository extends JpaRepository<DocMeta, Long> {
    @Query("SELECT t FROM DocMeta t LEFT JOIN FETCH t.tags")
    Set<DocMeta> findAllWithTags();
    @Query("SELECT t FROM DocMeta t LEFT JOIN FETCH t.tags WHERE t.date >= :startOfDay AND t.date < :endOfDay")
    Set<DocMeta> findAllWithTagsForToday(@Param("startOfDay") Timestamp startOfDay, @Param("endOfDay") Timestamp endOfDay);

    @Query("SELECT DISTINCT d FROM DocMeta d " +
            "LEFT JOIN FETCH d.digestCores " +
            "WHERE d IN :docMetas")
    List<DocMeta> findDocMetasWithCores(@Param("docMetas") List<DocMeta> docMetas);

    @Query("SELECT DISTINCT d.id FROM DocMeta d " +
            "LEFT JOIN d.tags t " +
            "LEFT JOIN d.packages p " +
            "WHERE " +
            "  (cast(:dateFrom as timestamp) IS NULL OR d.date >= :dateFrom) " +
            "  AND (cast(:dateTo as timestamp) IS NULL OR d.date <= :dateTo) " +
            "  AND (:languages IS NULL OR d.language IN :languages) " +
            "  AND (:sources IS NULL OR d.source IN :sources) " +
            "  AND (:tags IS NULL OR t.name IN :tags) " +
            "  AND (:packageNames IS NULL OR :userId IS NULL " +
            "     OR EXISTS (SELECT 1 FROM d.packages pkg " +
            "                WHERE pkg.id.name IN :packageNames AND pkg.owner.id = :userId))")
    List<Long> findFilteredDocIds(
            @Param("dateFrom") Timestamp dateFrom,
            @Param("dateTo") Timestamp dateTo,
            @Param("languages") List<Language> languages,
            @Param("sources") List<Source> sources,
            @Param("tags") List<String> tags,
            @Param("packageNames") List<String> packageNames,
            @Param("userId") Long userId);
}
