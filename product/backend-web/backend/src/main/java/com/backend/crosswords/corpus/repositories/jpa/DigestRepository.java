package com.backend.crosswords.corpus.repositories.jpa;

import com.backend.crosswords.corpus.models.Digest;
import com.backend.crosswords.corpus.models.DigestCore;
import com.backend.crosswords.corpus.models.DigestId;
import com.backend.crosswords.corpus.models.DigestSubscription;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Set;

public interface DigestRepository extends JpaRepository<Digest, DigestId> {
    Set<Digest> findAllByCoreIn(List<DigestCore> cores);
    Set<Digest> findAllBySubscriptionIn(List<DigestSubscription> subscriptions);
    @Query("""
        SELECT d
        FROM Digest d
        WHERE d.subscription.isPublic = true
        ORDER BY d.core.date DESC
    """)
    Slice<Digest> findAllPublicDigests(Pageable pageable);
    @Query("""
    SELECT d
    FROM Digest d
    WHERE d.id IN (
        SELECT DISTINCT di.id
        FROM Digest di
        LEFT JOIN di.subscription ds
        LEFT JOIN ds.subscriptionSettings dss
        WHERE
            (ds.isPublic = true)
            OR
            (dss.subscriber.id = :userId)
    )
    ORDER BY d.core.date DESC
""")
    Slice<Digest> findAllUsersDigests(@Param("userId") Long userId, Pageable pageable);
    @Query("""
    SELECT d
    FROM Digest d
    WHERE d.id IN (
        SELECT DISTINCT di.id
        FROM Digest di
        LEFT JOIN di.subscription ds
        LEFT JOIN ds.subscriptionSettings dss
        WHERE
            (ds.isPublic = false)
            AND
            (dss.subscriber.id = :userId)
    )
    ORDER BY d.core.date DESC
""")
    Slice<Digest> findAllPrivateUsersDigests(@Param("userId") Long userId, Pageable pageable);
}
