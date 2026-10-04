package com.backend.crosswords.admin.repositories;

import com.backend.crosswords.admin.models.TelegramLinkToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface TelegramLinkTokenRepository extends JpaRepository<TelegramLinkToken, Long> {

    Optional<TelegramLinkToken> findByToken(String token);

    Optional<TelegramLinkToken> findByUserIdAndTokenAndUsedFalseAndExpiresAtAfter(
            Long userId, String token, LocalDateTime now);

    @Modifying
    @Transactional
    @Query("UPDATE TelegramLinkToken t SET t.used = true WHERE t.token = :token")
    void markTokenAsUsed(@Param("token") String token);

    @Modifying
    @Transactional
    @Query("DELETE FROM TelegramLinkToken t WHERE t.userId = :userId")
    void deleteAllByUserId(@Param("userId") Long userId);

    @Modifying
    @Transactional
    @Query("DELETE FROM TelegramLinkToken t WHERE t.expiresAt < :now")
    void deleteExpiredTokens(@Param("now") LocalDateTime now);
}