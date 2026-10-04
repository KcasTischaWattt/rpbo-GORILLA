package com.backend.crosswords.admin.controllers;

import com.auth0.jwt.exceptions.TokenExpiredException;
import com.backend.crosswords.admin.dto.*;
import com.backend.crosswords.admin.models.CrosswordUserDetails;
import com.backend.crosswords.admin.models.User;
import com.backend.crosswords.admin.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.http.ConnectionClosedException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/users")
@Tag(name = "User controller", description = "Controller for all operations with users")
public class UserController {
    private final UserService userService;
    @Value("${backend-secret-key}")
    private String backendSecretKey;

    public UserController(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
    }

    private ResponseEntity<?> setCookies(HttpServletResponse response, List<String> jwt) {
        var accessTokenCookie = new Cookie("access_token", jwt.get(0));
        accessTokenCookie.setHttpOnly(true);
        accessTokenCookie.setSecure(true);
        accessTokenCookie.setPath("/");
        var refreshTokenCookie = new Cookie("refresh_token", jwt.get(1));
        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setSecure(true);
        refreshTokenCookie.setPath("/");
        response.addCookie(accessTokenCookie);
        response.addCookie(refreshTokenCookie);
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @Operation(
            summary = "Register a user by it's credentials",
            description = "This endpoint lets you register a user by it's credentials and other important information"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "You successfully registered the user by it's credentials"),
            @ApiResponse(responseCode = "400", description = "The user with such email or username have been already registered, or the credentials are incorrect")
    })
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody RegisterUserDTO registerUserDTO, HttpServletRequest request, HttpServletResponse response) {
        String ipAddress = request.getHeader("X-Forwarded-For");
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getRemoteAddr();
        }
        String userAgent = request.getHeader("User-Agent");
        List<String> jwt;
        try {
            jwt =  userService.registerUser(registerUserDTO, ipAddress, userAgent);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Incorrect credentials!");
        }
        return this.setCookies(response, jwt);
    }

    @Operation(
            summary = "Login the user by it's credentials",
            description = "This endpoint lets you login the user by it's credentials, email and username can be both used as user's login"

    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "You successfully login the user by it's credentials"),
            @ApiResponse(responseCode = "400", description = "The credentials are incorrect")
    })
    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody LoginUserDTO loginUserDTO, HttpServletRequest request, HttpServletResponse response) {
        String ipAddress = request.getHeader("X-Forwarded-For");
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getRemoteAddr();
        }
        String userAgent = request.getHeader("User-Agent");
        List<String> jwt;
        try {
            jwt =  userService.loginUser(loginUserDTO, ipAddress, userAgent);
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Incorrect credentials!");
        }
        return this.setCookies(response, jwt);
    }

    @Operation(
            summary = "Refresh the user by it's refresh token",
            description = "This endpoint lets you manually refresh the user by it's refresh token",
            deprecated = true
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "You successfully refreshed the user by it's refresh token"),
            @ApiResponse(responseCode = "400", description = "The credentials are incorrect")
    })
    @PostMapping("/refresh")
    public ResponseEntity<?> refreshUser(@RequestHeader("Authorization") String authHeader, HttpServletRequest request, HttpServletResponse response) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Missing or invalid Authorization header");
        }
        String authToken = authHeader.substring(7);
        if (!backendSecretKey.equals(authToken)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Your token is incorrect!");
        }
        String ipAddress = request.getHeader("X-Forwarded-For");
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getRemoteAddr();
        }
        String userAgent = request.getHeader("User-Agent");
        List<String> jwt;
        try {
            String token = null;
            if (request.getCookies() != null) {
                for (Cookie cookie : request.getCookies()) {
                    if ("refresh_token".equals(cookie.getName())) {
                        token = cookie.getValue();
                        break;
                    }
                }
            }
            if (token == null || token.isBlank()) {
                throw new BadCredentialsException("Incorrect credentials!");
            }
            jwt =  userService.refreshUser(token, ipAddress, userAgent);
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No such refresh token!");
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Incorrect credentials!");
        } catch (TokenExpiredException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage() + " At: " + e.getExpiredOn());
        }
        return this.setCookies(response, jwt);
    }
    @Operation(
            summary = "Check the user's authorities",
            description = "This endpoint lets you check the user's authorities, and get them in lowercase"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "You successfully checked the user's authorities", content = @Content(schema = @Schema(implementation = AuthorityDTO.class))),
            @ApiResponse(responseCode = "401", description = "The user is not authenticated")
    })
    @GetMapping("/check_auth")
    public ResponseEntity<?> checkUsersAuthorities() {
        List<String> authoritiesNames = userService.getAuthoritiesNamesByUser();
        return ResponseEntity.ok(new AuthorityDTO(authoritiesNames));
    }
    @Operation(
            summary = "Get user's email",
            description = "This endpoint lets you get user's email"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "You successfully get user's email", content = @Content(schema = @Schema(implementation = EmailDTO.class))),
            @ApiResponse(responseCode = "401", description = "You are trying to get user's email while not authenticated")
    })
    @GetMapping("/get_email")
    public ResponseEntity<?> getUsersEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CrosswordUserDetails crosswordUserDetails = (CrosswordUserDetails) authentication.getPrincipal();
        String email = userService.getUsersEmail(crosswordUserDetails.getUser());
        return ResponseEntity.ok(new EmailDTO(email));
    }
    @Operation(
            summary = "Check user's subscription settings",
            description = "This endpoint lets you check user's subscription settings by it's username/email"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "You successfully checked user's subscription settings", content = @Content(schema = @Schema(implementation = PersonalDigestSubscriptionSettingsDTO.class))),
            @ApiResponse(responseCode = "404", description = "There is no users with such username"),
            @ApiResponse(responseCode = "403", description = "This user can't be added into any subscriptions!"),
    })
    @PostMapping("/subscription_settings/check")
    public ResponseEntity<?> checkUsersSubscriptionSettings(@RequestBody UsernameDTO usernameDTO) {
        PersonalDigestSubscriptionSettingsDTO subscriptionSettingsDTO = null;
        try {
            subscriptionSettingsDTO = userService.checkUsersSubscriptionSettings(usernameDTO.getUsername());
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        }
        return ResponseEntity.ok(subscriptionSettingsDTO);
    }
    @Operation(
            summary = "Set user's subscription settings",
            description = "This endpoint lets you set user's subscription settings"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "You successfully set user's subscription settings"),
            @ApiResponse(responseCode = "401", description = "You are trying to set user's subscription settings while not authenticated"),
    })
    @PutMapping("/subscription_settings/set")
    public ResponseEntity<?> setUsersSubscriptionSettings(@RequestBody PersonalDigestSubscriptionSettingsDTO subscriptionSettingsDTO) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CrosswordUserDetails crosswordUserDetails = (CrosswordUserDetails) authentication.getPrincipal();
        userService.setUsersSubscriptionSettings(crosswordUserDetails.getUser(), subscriptionSettingsDTO.getSendToMail(), subscriptionSettingsDTO.getMobileNotifications(), subscriptionSettingsDTO.getPersonalSendToMail(), subscriptionSettingsDTO.getPersonalMobileNotifications(), subscriptionSettingsDTO.getSubscribable());
        return ResponseEntity.ok(HttpStatus.OK);
    }
    @Operation(
            summary = "Logout user",
            description = "This endpoint lets you logout user and erase cookies for him, remember that all tries of requests will automatically erase cookies, if the refresh token from cookies doesn't exist"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "You successfully logout user"),
            @ApiResponse(responseCode = "401", description = "You are trying to logout user while not authenticated"),
            @ApiResponse(responseCode = "404", description = "You are trying to logout user that hasn't been authenticated yet, this error will occur really rare and exist for just in case")
    })
    @PostMapping("/logout")
    public ResponseEntity<?> logoutUser(HttpServletRequest request, HttpServletResponse response) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CrosswordUserDetails crosswordUserDetails = (CrosswordUserDetails) authentication.getPrincipal();
        String ipAddress = request.getHeader("X-Forwarded-For");
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getRemoteAddr();
        }
        String userAgent = request.getHeader("User-Agent");
        try {
            userService.logoutUser(crosswordUserDetails.getUser(), ipAddress, userAgent);
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
        return this.setCookies(response, List.of("", ""));
    }
    @Operation(
            summary = "Fully logout user",
            description = "This endpoint lets you fully logout user, deleting all his refreshes and erase cookies for him, remember that all tries of requests will automatically erase cookies, if the refresh token from cookies doesn't exist"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "You successfully fully logout user"),
            @ApiResponse(responseCode = "401", description = "You are trying to fully logout user while not authenticated")
    })
    @PostMapping("/logout/full")
    public ResponseEntity<?> logoutUserFull(HttpServletRequest request, HttpServletResponse response) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CrosswordUserDetails crosswordUserDetails = (CrosswordUserDetails) authentication.getPrincipal();
        userService.logoutUserFull(crosswordUserDetails.getUser());
        return this.setCookies(response, List.of("", ""));
    }
    @Operation(
            summary = "Change user's password",
            description = "This endpoint lets you change user's password"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "You successfully changed user's password"),
            @ApiResponse(responseCode = "401", description = "You are trying to change user's password while not authenticated"),
            @ApiResponse(responseCode = "403", description = "An old password is incorrect"),
            @ApiResponse(responseCode = "400", description = "A new password can't be the same as an old password")
    })
    @PatchMapping("/change/password")
    public ResponseEntity<?> changeUsersPassword(@RequestBody ChangeUsersPasswordDTO changeUsersPasswordDTO) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CrosswordUserDetails crosswordUserDetails = (CrosswordUserDetails) authentication.getPrincipal();
        try {
            userService.changeUsersPassword(crosswordUserDetails.getUser(), changeUsersPasswordDTO.getOldPassword(), changeUsersPasswordDTO.getNewPassword());
        } catch (IllegalAccessException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
        return ResponseEntity.ok(HttpStatus.OK);
    }
    @Operation(
            summary = "Change user's email",
            description = "This endpoint lets you change user's email, remember that username also changes"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "You successfully changed user's email", content = @Content(schema = @Schema(implementation = NewEmailAndUsernameDTO.class))),
            @ApiResponse(responseCode = "401", description = "You are trying to change user's email while not authenticated"),
            @ApiResponse(responseCode = "400", description = "A new email can't be the same as an old email and can't be null or empty")
    })
    @PatchMapping("/change/email")
    public ResponseEntity<?> changeUsersEmail(@RequestBody ChangeUsersEmailDTO changeUsersEmailDTO) { // TODO добавить верификацию
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CrosswordUserDetails crosswordUserDetails = (CrosswordUserDetails) authentication.getPrincipal();
        User user = crosswordUserDetails.getUser();
        try {
            userService.changeUsersEmail(user, changeUsersEmailDTO.getNewEmail());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
        return ResponseEntity.ok(new NewEmailAndUsernameDTO(user.getEmail(), user.getUsername()));
    }
    @Operation(
            summary = "Get user's personal info",
            description = "This endpoint lets you get user's personal info"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "You successfully get user's personal info", content = @Content(schema = @Schema(implementation = GetPersonalInfoDTO.class))),
            @ApiResponse(responseCode = "401", description = "You are trying to get user's personal info while not authenticated"),
    })
    @GetMapping("/personal_info")
    public ResponseEntity<?> getUsersPersonalInfo() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CrosswordUserDetails crosswordUserDetails = (CrosswordUserDetails) authentication.getPrincipal();
        GetPersonalInfoDTO personalInfoDTO = userService.getUsersPersonalInfoAndTransformIntoDTO(crosswordUserDetails.getUser());
        return ResponseEntity.ok(personalInfoDTO);
    }
    @Operation(
            summary = "Check user's verification",
            description = "This endpoint lets you check if user's email is verificated"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "You successfully checked user's verification", content = @Content(schema = @Schema(implementation = CheckUsersVerificationDTO.class))),
            @ApiResponse(responseCode = "401", description = "You are trying to check user's verification while not authenticated")
    })
    @GetMapping("/check_verification")
    public ResponseEntity<?> checkUsersEmailVerification() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CrosswordUserDetails crosswordUserDetails = (CrosswordUserDetails) authentication.getPrincipal();
        var checkVerification = userService.checkUsersEmailVerification(crosswordUserDetails.getUser());
        return ResponseEntity.ok(checkVerification);
    }
    @Operation(
            summary = "Send user's verification code",
            description = "This endpoint lets you send user's verification code on his email"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "You successfully sent user's verification code and got his email", content = @Content(schema = @Schema(implementation = VerificatingEmailDTO.class))),
            @ApiResponse(responseCode = "401", description = "You are trying to send user's verification code while not authenticated"),
            @ApiResponse(responseCode = "404", description = "Connection with mailman error")
    })
    @PostMapping("/verification_code/send")
    public ResponseEntity<?> sendVerificationCode() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CrosswordUserDetails crosswordUserDetails = (CrosswordUserDetails) authentication.getPrincipal();
        try {
            var verificatingEmail = userService.sendVerificationCodeAndReturnVerificatingEmailInDTO(crosswordUserDetails.getUser());
            return ResponseEntity.ok(verificatingEmail);
        } catch (ConnectionClosedException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
    @Operation(
            summary = "Check user's verification code",
            description = "This endpoint lets you check user's verification code"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "You successfully checked user's verification code"),
            @ApiResponse(responseCode = "401", description = "You are trying to check user's verification code while not authenticated"),
            @ApiResponse(responseCode = "404", description = "There is no verification code for this user"),
            @ApiResponse(responseCode = "400", description = "Wrong verification code")
    })
    @PostMapping("/verification_code/check")
    public ResponseEntity<?> checkVerificationCode(@RequestBody CheckVerificationCodeDTO checkVerificationCodeDTO) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CrosswordUserDetails crosswordUserDetails = (CrosswordUserDetails) authentication.getPrincipal();
        try {
            userService.checkVerificationCodeForUser(checkVerificationCodeDTO.getCode(), crosswordUserDetails.getUser());
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
        return ResponseEntity.ok(HttpStatus.OK);
    }
    @Operation(
            summary = "Create a fcm token for user",
            description = "This endpoint lets you create a fcm token for user"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "You successfully created a fcm token for user"),
            @ApiResponse(responseCode = "401", description = "You are trying to create a fcm token for user while not authenticated")
    })
    @PostMapping("/fcm_token/create")
    public ResponseEntity<?> createFcmTokenForUser(@RequestParam String fcmToken) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CrosswordUserDetails crosswordUserDetails = (CrosswordUserDetails) authentication.getPrincipal();
        userService.createFcmTokenForUser(crosswordUserDetails.getUser(), fcmToken);
        return ResponseEntity.ok(HttpStatus.OK);
    }
    @Operation(
            summary = "Delete a fcm token for user",
            description = "This endpoint lets you delete a fcm token for user"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "You successfully deleted a fcm token for user"),
            @ApiResponse(responseCode = "404", description = "There is no such fcm tokens for this user"),
            @ApiResponse(responseCode = "401", description = "You are trying to delete a fcm token for user while not authenticated")
    })
    @DeleteMapping("/fcm_token/delete")
    public ResponseEntity<?> deleteFcmToken(@RequestParam String fcmToken) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CrosswordUserDetails crosswordUserDetails = (CrosswordUserDetails) authentication.getPrincipal();
        try {
            userService.deleteFcmTokenForUser(crosswordUserDetails.getUser(), fcmToken);
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @Operation(
            summary = "Generate Telegram link token",
            description = "Generates a token for linking Telegram account"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Token generated successfully"),
            @ApiResponse(responseCode = "401", description = "Not authenticated")
    })
    @PostMapping("/telegram/generate-token")
    public ResponseEntity<?> generateTelegramLinkToken() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CrosswordUserDetails crosswordUserDetails = (CrosswordUserDetails) authentication.getPrincipal();
        User user = crosswordUserDetails.getUser();

        String token = userService.generateTelegramLinkToken(user.getId());

        return ResponseEntity.ok(Map.of(
                "token", token,
                "botUsername", "crosswords_app_bot", // Имя вашего бота
                "link", "https://t.me/crosswords_app_bot?start=bind_" + user.getId() + "_" + token
        ));
    }
    //
    // TODO добавить удаление пользователя, учтя тот факт, что перед удалением необходимо очистить связанные с ним данные
    /*@DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUserById(@PathVariable Long id) {
        userService.deleteUserById(id);
        return ResponseEntity.ok(HttpStatus.OK);
    }*/
}
