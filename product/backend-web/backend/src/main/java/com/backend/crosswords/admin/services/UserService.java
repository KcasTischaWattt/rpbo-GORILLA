package com.backend.crosswords.admin.services;

import com.backend.crosswords.admin.dto.*;
import com.backend.crosswords.admin.enums.RoleEnum;
import com.backend.crosswords.admin.models.CrosswordUserDetails;
import com.backend.crosswords.admin.models.RefreshToken;
import com.backend.crosswords.admin.models.TelegramLinkToken;
import com.backend.crosswords.admin.models.User;
import com.backend.crosswords.admin.repositories.TelegramLinkTokenRepository;
import com.backend.crosswords.admin.repositories.UserRepository;
import com.backend.crosswords.config.JWTUtil;
import com.backend.crosswords.corpus.models.Package;
import com.backend.crosswords.corpus.models.DocRating;
import com.backend.crosswords.corpus.services.PackageService;
import org.apache.http.ConnectionClosedException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class UserService {
    private final ModelMapper modelMapper;
    private final UserRepository userRepository;
    private final TelegramLinkTokenRepository telegramLinkTokenRepository;
    private final AuthenticationManager authenticationManager;
    private final JWTUtil jwtUtil;
    private final RefreshTokenService refreshTokenService;
    private final PasswordEncoder passwordEncoder;
    private final PackageService packageService;
    private final VerifyCodeService verifyCodeService;
    private final FcmTokenService fcmTokenService;
    @Value("${default-admins-password}")
    private String defaultAdminsPassword;

    public UserService(ModelMapper modelMapper, UserRepository userRepository, TelegramLinkTokenRepository telegramLinkTokenRepository, AuthenticationManager authenticationManager, JWTUtil jwtUtil, RefreshTokenService refreshTokenService, PasswordEncoder passwordEncoder, PackageService packageService, VerifyCodeService verifyCodeService, FcmTokenService fcmTokenService) {
        this.modelMapper = modelMapper;
        this.userRepository = userRepository;
        this.telegramLinkTokenRepository = telegramLinkTokenRepository;
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.refreshTokenService = refreshTokenService;
        this.passwordEncoder = passwordEncoder;
        this.packageService = packageService;
        this.verifyCodeService = verifyCodeService;
        this.fcmTokenService = fcmTokenService;
    }

    @Transactional
    public List<String> registerUser(RegisterUserDTO registerUserDTO, String ipAddress, String userAgent) {
        var usernameFromEmail = registerUserDTO.getEmail().split("@")[0];
        var user = modelMapper.map(registerUserDTO, User.class);
        user.setUsername(usernameFromEmail);
        if (userRepository.existsUserByUsername(user.getUsername())) {
            throw new IllegalArgumentException("This username is already existed");
        }
        if (userRepository.existsUserByEmail(user.getEmail())) {
            throw new IllegalArgumentException("This email is already existed");
        }
        user.setPassword(passwordEncoder.encode(registerUserDTO.getPassword()));
        for (var role : RoleEnum.values()) {
            for (var username : role.getUsersWhiteList()) {
                if (Objects.equals(username.getEmail(), user.getEmail())) {
                    user.setRole(role);
                }
            }
        }

        user.setSubscribable(true);
        user.setPersonalMobileNotifications(false);
        user.setPersonalSendToMail(false);
        user.setMobileNotifications(false);
        user.setSendToMail(false);

        user = userRepository.save(user);

        packageService.createPackage(Package.favouritesName, user);
        return this.loginUser(new LoginUserDTO(registerUserDTO.getEmail(), registerUserDTO.getPassword(), registerUserDTO.getFcmToken()), ipAddress, userAgent);
    }

    public List<String> loginUser(LoginUserDTO loginUserDTO, String ipAddress, String userAgent) {
        UsernamePasswordAuthenticationToken authInputToken = new UsernamePasswordAuthenticationToken(loginUserDTO.getUsername(), loginUserDTO.getPassword());
        authenticationManager.authenticate(authInputToken);
        var accessToken = jwtUtil.generateAccessToken(loginUserDTO.getUsername());
        var user = userRepository.findByUsernameOrEmail(loginUserDTO.getUsername(), loginUserDTO.getUsername()).orElseThrow();
        RefreshToken refreshToken;
        var optionalRefreshToken = refreshTokenService.checkExistingRefreshToken(ipAddress, userAgent, user);
        if (optionalRefreshToken == null) {
            refreshToken = refreshTokenService.generateRefreshToken(ipAddress, userAgent, user);
        } else {
            refreshToken = optionalRefreshToken;
        }
        var fcmToken = loginUserDTO.getFcmToken();
        if (fcmToken != null && !fcmToken.isEmpty()) {
            fcmTokenService.createFcmTokenForUser(user, fcmToken);
        }
        List<String> jwt = new ArrayList<>();
        jwt.add(accessToken);
        jwt.add(refreshToken.getToken());
        return jwt;
    }

    public List<String> refreshUser(String oldRefreshToken, String ipAddress, String userAgent) {
        var newRefreshToken = refreshTokenService.refreshUser(oldRefreshToken, ipAddress, userAgent);
        var accessToken = jwtUtil.generateAccessToken(newRefreshToken.getUser().getUsername());
        List<String> jwt = new ArrayList<>();
        jwt.add(accessToken);
        jwt.add(newRefreshToken.getToken());
        return jwt;
    }

    @Transactional
    public void removeRating(DocRating rating) {
        var user = rating.getUser();
        user.getRatings().remove(rating);
        userRepository.save(user);
    }

    public User loadUserById(Long id) {
        return userRepository.findById(id).orElseThrow();
    }

    public List<String> getAuthoritiesNamesByUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CrosswordUserDetails crosswordUserDetails = (CrosswordUserDetails) authentication.getPrincipal();
        List<String> authoritiesNames = new ArrayList<>();
        for (var authority : crosswordUserDetails.getAuthorities()) {
            authoritiesNames.add(authority.getAuthority().toLowerCase());
        }
        return authoritiesNames;
    }

    public void createDefaultUsers() {
        for (var role : RoleEnum.values()) {
            for (var registeredUser : role.getUsersWhiteList()) {
                var email = registeredUser.getEmail();
                var user = userRepository.findByUsernameOrEmail(email, email);
                if (user.isPresent() && !user.get().getRole().name().equals(role.name())) {
                    user.get().setRole(role);
                }
            }
        }
        for (var registeredUser : RoleEnum.ROLE_ADMIN.getUsersWhiteList()) {
            var email = registeredUser.getEmail();
            if (userRepository.findByUsernameOrEmail(email, email).isEmpty()) {
                // TODO сделать пароль admin настраиваемым через параметры среды запуска или придумать другой более безопасный и удобный способ
                var user = userRepository.save(new User(registeredUser.getName(), registeredUser.getSurname(), registeredUser.getEmail(), passwordEncoder.encode(defaultAdminsPassword), RoleEnum.ROLE_ADMIN));
                packageService.createPackage(Package.favouritesName, user);
            }
        }
    }

    public User getUserByUsername(String username) {
        return userRepository.findByUsernameOrEmail(username, username).orElseThrow(() -> new NoSuchElementException("There is no users with such username/email!"));
    }

    public String getUsersEmail(User user) {
        return user.getEmail();
    }

    public PersonalDigestSubscriptionSettingsDTO checkUsersSubscriptionSettings(String username) {
        var user = userRepository.findByUsernameOrEmail(username, username).orElseThrow(() -> new NoSuchElementException("There is no users with such username/email!"));
        if (!user.getSubscribable()) {
            throw new IllegalArgumentException("This user can't be added into any subscriptions!");
        }
        return new PersonalDigestSubscriptionSettingsDTO(user.getSendToMail(), user.getMobileNotifications(), user.getPersonalSendToMail(), user.getPersonalMobileNotifications(), user.getSubscribable());
    }

    public void setUsersSubscriptionSettings(User user, Boolean sendToMail, Boolean mobileNotifications, Boolean personalSendToMail, Boolean personalMobileNotifications, Boolean subscribable) {
        user.setSendToMail(sendToMail);
        user.setMobileNotifications(mobileNotifications);
        user.setPersonalSendToMail(personalSendToMail);
        user.setPersonalMobileNotifications(personalMobileNotifications);
        user.setSubscribable(subscribable);
        userRepository.save(user);
    }

    public void logoutUser(User user, String ipAddress, String userAgent) {
        refreshTokenService.deleteRefreshForUser(user, ipAddress, userAgent);
    }

    public void logoutUserFull(User user) {
        refreshTokenService.deleteAllRefreshesForUser(user);
    }

    public void changeUsersPassword(User user, String oldPassword, String newPassword) throws IllegalAccessException, IllegalArgumentException {
        String actualPassword = user.getPassword();
        if (!passwordEncoder.matches(oldPassword, actualPassword)) {
            throw new IllegalAccessException("Incorrect password!");
        }
        if (oldPassword.equals(newPassword)) {
            throw new IllegalArgumentException("New password cant be the same as an old password");
        }
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    public void changeUsersEmail(User user, String newEmail) {
        if (newEmail == null || newEmail.equals("")) {
            throw new IllegalArgumentException("Incorrect email!");
        }
        if (user.getEmail().equals(newEmail)) {
            throw new IllegalArgumentException("A new email can't be the same as an old one!");
        }
        if (userRepository.existsUserByEmail(newEmail)) {
            throw new IllegalArgumentException("A user with such email already exists!");
        }
        user.setEmail(newEmail);
        user.setUsername(newEmail.split("@")[0]);
        user.setVerified(false);
        userRepository.save(user);
    }
    public GetPersonalInfoDTO getUsersPersonalInfoAndTransformIntoDTO(User user) {
        return new GetPersonalInfoDTO(user.getName(), user.getSurname(), user.getUsername(), user.getEmail(), user.getSendToMail(), user.getMobileNotifications(), user.getPersonalSendToMail(), user.getPersonalMobileNotifications(), user.getSubscribable(), user.getTelegramId() != null);
    }

    public CheckUsersVerificationDTO checkUsersEmailVerification(User user) {
        return new CheckUsersVerificationDTO(!user.getVerified());
    }

    public VerificatingEmailDTO sendVerificationCodeAndReturnVerificatingEmailInDTO(User user) throws ConnectionClosedException {
        user = userRepository.findById(user.getId()).orElseThrow();
        var email = user.getEmail();
        verifyCodeService.sendEmailWithVerificationCode(user);
        return new VerificatingEmailDTO(email);
    }

    public void checkVerificationCodeForUser(String checkingCode, User user) throws NoSuchElementException, IllegalArgumentException {
        user = userRepository.findById(user.getId()).orElseThrow();
        if (verifyCodeService.checkVerificationCodeForUser(checkingCode, user)) {
            user.setVerified(true);
            userRepository.save(user);
        }
    }

    public void createFcmTokenForUser(User user, String fcmToken) {
        fcmTokenService.createFcmTokenForUser(user, fcmToken);
    }

    public void deleteFcmTokenForUser(User user, String fcmToken) throws NoSuchElementException {
        fcmTokenService.deleteFcmTokenFormUser(user, fcmToken);
    }

    @Transactional
    public String generateTelegramLinkToken(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User not found"));

        // Удаляем старые неиспользованные токены пользователя
        telegramLinkTokenRepository.deleteAllByUserId(userId);

        // Генерируем новый токен
        String token = UUID.randomUUID().toString();
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expiresAt = now.plusMinutes(10); // Токен действителен 10 минут

        TelegramLinkToken linkToken = new TelegramLinkToken(userId, token, now, expiresAt);
        telegramLinkTokenRepository.save(linkToken);

        return token;
    }

    public boolean validateTelegramLinkToken(Long userId, String token) {
        return telegramLinkTokenRepository
                .findByUserIdAndTokenAndUsedFalseAndExpiresAtAfter(userId, token, LocalDateTime.now())
                .isPresent();
    }

    @Transactional
    public User linkTelegramToUser(Long userId, Long telegramId) {
        // Проверяем, не привязан ли уже этот Telegram ID к другому пользователю
        userRepository.findByTelegramId(telegramId).ifPresent(u -> {
            throw new IllegalArgumentException("This Telegram account is already linked to another user");
        });

        // Находим пользователя
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User not found"));

        // Проверяем, не привязан ли уже Telegram к этому пользователю
        if (user.getTelegramId() != null) {
            throw new IllegalArgumentException("User already has a linked Telegram account");
        }

        // Привязываем Telegram ID
        userRepository.updateTelegramId(userId, telegramId);

        // Возвращаем обновленного пользователя
        return userRepository.findById(userId).get();
    }

    public User findByTelegramId(Long telegramId) {
        return userRepository.findByTelegramId(telegramId)
                .orElseThrow(() -> new NoSuchElementException("User with Telegram ID " + telegramId + " not found"));
    }

    @Transactional
    public void unlinkTelegram(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User not found"));

        if (user.getTelegramId() == null) {
            throw new IllegalArgumentException("User doesn't have linked Telegram account");
        }

        userRepository.unlinkTelegramId(user.getTelegramId());
    }

    public boolean hasLinkedTelegram(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User not found"));
        return user.getTelegramId() != null;
    }

    @Scheduled(cron = "0 0 * * * *")
    @Transactional
    public void cleanupExpiredTokens() {
        telegramLinkTokenRepository.deleteExpiredTokens(LocalDateTime.now());
    }

    public void saveUser(User user) {
        userRepository.save(user);
    }
}
