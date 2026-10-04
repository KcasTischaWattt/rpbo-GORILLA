package com.backend.crosswords.admin.repositories;

import com.backend.crosswords.admin.models.RefreshToken;
import com.backend.crosswords.admin.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByToken(String token);
    Boolean existsByUserAndUserAgentAndIp(User user, String userAgent, String ipAddress);
    List<RefreshToken> findByUserAndUserAgentAndIp(User user, String userAgent, String ipAddress);
    List<RefreshToken> findAllByUser(User user);
}
