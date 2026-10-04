package com.backend.crosswords.admin.repositories;

import com.backend.crosswords.admin.models.FcmToken;
import com.backend.crosswords.admin.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface FcmTokenRepository extends JpaRepository<FcmToken, Long> {
    Boolean existsByUserAndToken(User user, String token);
    Optional<FcmToken> findByToken(String token);
    Optional<FcmToken> findByUserAndToken(User user, String token);
    Set<FcmToken> findByUser(User user);
    List<FcmToken> findAllByUserIn(List<User> users);
}
