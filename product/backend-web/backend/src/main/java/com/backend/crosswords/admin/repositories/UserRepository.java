package com.backend.crosswords.admin.repositories;

import com.backend.crosswords.admin.models.User;
import com.backend.crosswords.corpus.models.DocMeta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Boolean existsUserByUsername(String username);
    Boolean existsUserByEmail(String email);
    Optional<User> findByUsernameOrEmail(String username, String email);
    Optional<User> findByTelegramId(Long telegramId);

    @Modifying
    @Transactional
    @Query("UPDATE User u SET u.telegramId = :telegramId WHERE u.id = :userId")
    void updateTelegramId(@Param("userId") Long userId, @Param("telegramId") Long telegramId);

    @Modifying
    @Transactional
    @Query("UPDATE User u SET u.telegramId = NULL WHERE u.telegramId = :telegramId")
    void unlinkTelegramId(@Param("telegramId") Long telegramId);
}
