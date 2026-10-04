package com.backend.crosswords.admin.services;

import com.backend.crosswords.admin.models.FcmToken;
import com.backend.crosswords.admin.models.User;
import com.backend.crosswords.admin.repositories.FcmTokenRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class FcmTokenService {
    private final FcmTokenRepository fcmTokenRepository;

    public FcmTokenService(FcmTokenRepository fcmTokenRepository) {
        this.fcmTokenRepository = fcmTokenRepository;
    }

    @Transactional
    public void createFcmTokenForUser(User user, String fcmToken) {
        if (!fcmTokenRepository.existsByUserAndToken(user, fcmToken)) {
            fcmTokenRepository.save(new FcmToken(fcmToken, user));
        }
    }

    public List<FcmToken> getTokensByUsers(List<User> users) {
        return fcmTokenRepository.findAllByUserIn(users);
    }

    public void deleteAllExpiredTokens(List<FcmToken> expiredFcmTokens) {
        if (!expiredFcmTokens.isEmpty()) {
            fcmTokenRepository.deleteAll(expiredFcmTokens);
        }
    }

    public void deleteFcmTokenFormUser(User user, String fcmToken) throws NoSuchElementException {
        var fcmExistence = fcmTokenRepository.findByUserAndToken(user, fcmToken);
        if (fcmExistence.isPresent()) {
            fcmTokenRepository.delete(fcmExistence.get());
        } else {
            throw new NoSuchElementException("There is no such tokens for this user!");
        }
    }
}
