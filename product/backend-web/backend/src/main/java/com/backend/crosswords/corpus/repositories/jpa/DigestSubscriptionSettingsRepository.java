package com.backend.crosswords.corpus.repositories.jpa;

import com.backend.crosswords.admin.models.User;
import com.backend.crosswords.corpus.models.DigestSubscription;
import com.backend.crosswords.corpus.models.DigestSubscriptionSettings;
import com.backend.crosswords.corpus.models.DigestSubscriptionSettingsId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Set;

public interface DigestSubscriptionSettingsRepository extends JpaRepository<DigestSubscriptionSettings, DigestSubscriptionSettingsId> {
    List<DigestSubscriptionSettings> findAllByDigestSubscription(DigestSubscription subscription);
    List<DigestSubscriptionSettings> findAllBySubscriber(User user);
    Set<DigestSubscriptionSettings> findAllBySubscriberOrDigestSubscription_IsPublic(User user, Boolean isPublic);
    @Query("SELECT s.digestSubscription " +
            "FROM DigestSubscriptionSettings s " +
            "WHERE s.subscriber = :user")
    Set<DigestSubscription> findDigestSubscriptionsByUser(@Param("user") User user);
}
