package com.backend.crosswords.admin.controllers;

import com.backend.crosswords.admin.dto.TelegramLinkResponseDTO;
import com.backend.crosswords.admin.models.User;
import com.backend.crosswords.admin.services.UserService;
import com.backend.crosswords.corpus.services.DigestService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/users/telegram")
public class TelegramController {

    private final UserService userService;
    private final DigestService digestService;

    @Value("${backend-secret-key}")
    private String backendSecretKey;

    public TelegramController(UserService userService, DigestService digestService) {
        this.userService = userService;
        this.digestService = digestService;
    }

    private boolean isAuthorized(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return false;
        }
        String token = authHeader.substring(7);
        return backendSecretKey.equals(token);
    }

    @Operation(summary = "Link Telegram account to user")
    @PostMapping("/link")
    public ResponseEntity<?> linkTelegram(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody Map<String, Object> payload) {

        if (!isAuthorized(authHeader)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new TelegramLinkResponseDTO(false, null, null, "Invalid secret key"));
        }

        try {
            Long telegramId = ((Number) payload.get("telegramId")).longValue();
            Long userId = ((Number) payload.get("userId")).longValue();
            String linkToken = (String) payload.get("linkToken");

            // Проверяем токен
            if (!userService.validateTelegramLinkToken(userId, linkToken)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new TelegramLinkResponseDTO(false, null, null, "Invalid or expired token"));
            }

            // Привязываем Telegram
            User user = userService.linkTelegramToUser(userId, telegramId);

            return ResponseEntity.ok(new TelegramLinkResponseDTO(
                    true, user.getId(), user.getName(), null
            ));

        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new TelegramLinkResponseDTO(false, null, null, e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new TelegramLinkResponseDTO(false, null, null, e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new TelegramLinkResponseDTO(false, null, null, "Internal server error"));
        }
    }

    @Operation(summary = "Get user by Telegram ID")
    @GetMapping("/{telegramId}")
    public ResponseEntity<?> getUserByTelegramId(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Long telegramId) {

        if (!isAuthorized(authHeader)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid secret key");
        }

        try {
            User user = userService.findByTelegramId(telegramId);

            Map<String, Object> response = new HashMap<>();
            response.put("id", user.getId());
            response.put("username", user.getUsername());
            response.put("email", user.getEmail());
            response.put("name", user.getName());
            response.put("surname", user.getSurname());
            response.put("verified", user.getVerified());
            response.put("telegramNotifications", user.getTelegramNotifications());

            return ResponseEntity.ok(response);

        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "User not found"));
        }
    }

    @Operation(summary = "Unlink Telegram from user")
    @DeleteMapping("/unlink/{userId}")
    public ResponseEntity<?> unlinkTelegram(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Long userId) {

        if (!isAuthorized(authHeader)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid secret key");
        }

        try {
            userService.unlinkTelegram(userId);
            return ResponseEntity.ok(Map.of("success", true));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("success", false, "error", e.getMessage()));
        }
    }

    // Добавить новые методы в TelegramController.java

    @Operation(summary = "Get user's digests for Telegram bot")
    @GetMapping("/digests")
    public ResponseEntity<?> getUserDigestsForTelegram(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam Long telegramId) {

        if (!isAuthorized(authHeader)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid secret key");
        }

        try {
            User user = userService.findByTelegramId(telegramId);
            // Получить дайджесты пользователя
            var digests = digestService.getAllAvailableDigests(user, 0, 10);
            return ResponseEntity.ok(digests);
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }
    }

    @Operation(summary = "Get digest by ID for Telegram bot")
    @GetMapping("/digest/{digestId}")
    public ResponseEntity<?> getDigestForTelegram(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable String digestId,
            @RequestParam Long telegramId) {

        if (!isAuthorized(authHeader)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid secret key");
        }

        try {
            User user = userService.findByTelegramId(telegramId);
            var digest = digestService.getDigestByIdAndTransformIntoDTO(digestId, user);
            return ResponseEntity.ok(digest);
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Digest not found");
        } catch (IllegalAccessException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied");
        }
    }

    @Operation(summary = "Update Telegram notification settings")
    @PutMapping("/settings")
    public ResponseEntity<?> updateTelegramSettings(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody Map<String, Object> settings) {

        if (!isAuthorized(authHeader)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid secret key");
        }

        try {
            Long telegramId = ((Number) settings.get("telegramId")).longValue();
            Boolean telegramNotifications = (Boolean) settings.get("telegramNotifications");

            User user = userService.findByTelegramId(telegramId);
            user.setTelegramNotifications(telegramNotifications);
            userService.saveUser(user);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "telegramNotifications", telegramNotifications
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("success", false, "error", e.getMessage()));
        }
    }
}