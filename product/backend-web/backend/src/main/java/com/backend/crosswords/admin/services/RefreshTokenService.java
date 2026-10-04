package com.backend.crosswords.admin.services;

import com.auth0.jwt.exceptions.TokenExpiredException;
import com.backend.crosswords.admin.models.RefreshToken;
import com.backend.crosswords.admin.models.User;
import com.backend.crosswords.admin.repositories.RefreshTokenRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.*;

@Service
public class RefreshTokenService {
    @Value("${jwt_refreshExpirationDate}")
    private Integer refreshExpirationMinutes;
    private final RefreshTokenRepository refreshTokenRepository;
    private final TaskScheduler taskScheduler;

    public RefreshTokenService(RefreshTokenRepository refreshTokenRepository, TaskScheduler taskScheduler) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.taskScheduler = taskScheduler;
    }
    @Transactional
    public RefreshToken generateRefreshToken(String ipAddress, String userAgent, User user) {
        RefreshToken refreshToken = new RefreshToken(UUID.randomUUID().toString(), ipAddress, userAgent, user,
                ZonedDateTime.now().plusMinutes(refreshExpirationMinutes).toInstant());
        return refreshTokenRepository.save(refreshToken);
    }

    @Transactional
    public RefreshToken refreshUser(String token, String ipAddress, String userAgent) throws NoSuchElementException, TokenExpiredException {
        var oldToken = refreshTokenRepository.findByToken(token).orElseThrow();
        if (!Objects.equals(oldToken.getIp(), ipAddress) || !Objects.equals(oldToken.getUserAgent(), userAgent)) {
            System.out.println("Подозрительная активность!!!!");
            // TODO добавить обработчик подозрительной активности
        }
        if (oldToken.getExpiryDate().isBefore(ZonedDateTime.now().toInstant())) {
            refreshTokenRepository.delete(oldToken);
            throw new TokenExpiredException("Your refresh token is expired!", oldToken.getExpiryDate());
        }
        if (oldToken.getNewRefresh() == null) {
            User user = oldToken.getUser();
            var newToken = this.generateRefreshToken(ipAddress, userAgent, user);
            oldToken.setNewRefresh(newToken);
            refreshTokenRepository.saveAndFlush(oldToken);
            taskScheduler.schedule(
                    () -> this.safeDeleteToken(oldToken.getId(), oldToken.getVersion()),
                    Instant.now().plusSeconds(5)
            );
            return newToken;
        } else {
            return oldToken.getNewRefresh();
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void safeDeleteToken(Long tokenId, Long version) {
        try {
            refreshTokenRepository.findById(tokenId)
                    .filter(t -> t.getVersion().equals(version))
                    .ifPresent(t -> {
                        refreshTokenRepository.delete(t);
                        refreshTokenRepository.flush();
                    });
        } catch (ObjectOptimisticLockingFailureException ex) {
        }
    }

    public void deleteRefreshTokenById(Long tokenId) {
        var optionalToken = refreshTokenRepository.findById(tokenId);
        optionalToken.ifPresent(refreshTokenRepository::delete);
        refreshTokenRepository.flush();
    }

    public RefreshToken checkExistingRefreshToken(String ipAddress, String userAgent, User user) {
        List<RefreshToken> tokens = refreshTokenRepository
                .findByUserAndUserAgentAndIp(user, userAgent, ipAddress);
        if (tokens.isEmpty()) {
            return null;
        }
        return tokens.get(0);
    }

    public void deleteRefreshForUser(User user, String ipAddress, String userAgent) {
        var refresh = refreshTokenRepository.findByUserAndUserAgentAndIp(user, userAgent, ipAddress);
        if (refresh.isEmpty()) {
            throw new NoSuchElementException("There is no such authorized users!");
        }
        refreshTokenRepository.deleteAll(refresh);
    }

    public void deleteAllRefreshesForUser(User user) {
        var refreshes = refreshTokenRepository.findAllByUser(user);
        refreshTokenRepository.deleteAll(refreshes);
    }
}
