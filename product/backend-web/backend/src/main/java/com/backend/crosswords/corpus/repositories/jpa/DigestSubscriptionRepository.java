package com.backend.crosswords.corpus.repositories.jpa;

import com.backend.crosswords.corpus.models.Digest;
import com.backend.crosswords.corpus.models.DigestSubscription;
import com.backend.crosswords.corpus.models.DigestTemplate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Set;

public interface DigestSubscriptionRepository extends JpaRepository<DigestSubscription, Long> {
    List<DigestSubscription> findAllByTemplate(DigestTemplate template);
    @Query("SELECT s FROM DigestSubscription s LEFT JOIN FETCH s.subscriptionSettings WHERE s.template = :template")
    List<DigestSubscription> findAllByTemplateWithSettings(DigestTemplate template);
    Set<DigestSubscription> findAllByIsPublic(Boolean isPublic);
    @Query("""
        SELECT d
        FROM DigestSubscription ds
        JOIN ds.digests d
        JOIN FETCH d.core c
        WHERE ds.id = :subscriptionId
        ORDER BY c.date DESC
    """)
    Slice<Digest> findDigestsBySubscriptionId(
            @Param("subscriptionId") Long subscriptionId,
            Pageable pageable
    );
    @Query("""
        SELECT COALESCE(AVG(r.digestCoreRating), -1.0)
        FROM DigestSubscription ds
        JOIN ds.digests d
        JOIN d.core c
        LEFT JOIN c.ratings r
        WHERE ds.id = :subscriptionId
    """)
    Double calculateAverageRating(@Param("subscriptionId") Long subscriptionId);
    @Query("""
    SELECT ds, COALESCE(AVG(r.digestCoreRating), -1.0)
    FROM DigestSubscription ds
    JOIN ds.digests d
    JOIN d.core c
    LEFT JOIN c.ratings r
    WHERE ds.isPublic = true
    GROUP BY ds
    ORDER BY COALESCE(AVG(r.digestCoreRating), -1.0) DESC
""")
    List<DigestSubscription> findMostRatedSubscriptions(Pageable pageable);
}
