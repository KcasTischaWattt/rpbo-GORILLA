package com.backend.crosswords.config;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.backend.crosswords.admin.models.CrosswordUserDetails;
import com.backend.crosswords.admin.models.RefreshToken;
import com.backend.crosswords.admin.services.CrosswordUserDetailsService;
import com.backend.crosswords.admin.services.RefreshTokenService;
import com.backend.crosswords.admin.services.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.NoSuchElementException;

@Component
public class JWTFilter extends OncePerRequestFilter {
    private final JWTUtil jwtUtil;
    private final CrosswordUserDetailsService crosswordUserDetailsService;
    private final RefreshTokenService refreshTokenService;

    public JWTFilter(JWTUtil jwtUtil, CrosswordUserDetailsService crosswordUserDetailsService, RefreshTokenService refreshTokenService) {
        this.jwtUtil = jwtUtil;
        this.crosswordUserDetailsService = crosswordUserDetailsService;
        this.refreshTokenService = refreshTokenService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String accessToken = null;
        String oldRefreshToken = null;
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("access_token".equals(cookie.getName())) {
                    accessToken = cookie.getValue();
                    if (oldRefreshToken != null) {
                        break;
                    }
                } else if ("refresh_token".equals(cookie.getName())) {
                    oldRefreshToken = cookie.getValue();
                    if (accessToken != null) {
                        break;
                    }
                }
            }
        }

        if (accessToken != null && !accessToken.isBlank()) {
            try {
                this.validateJWTAndAuthenticate(accessToken, request);
            } catch (UsernameNotFoundException | JWTVerificationException exception) {
                refreshUser(oldRefreshToken, request, response);
            } catch (IllegalAccessException e) {
                this.setCookies(response, "", "");
                AnonymousAuthenticationToken anonymousToken = new AnonymousAuthenticationToken(
                        "anonymous", "anonymousUser", AuthorityUtils.createAuthorityList("ROLE_ANONYMOUS"));
                SecurityContextHolder.getContext().setAuthentication(anonymousToken);
            }
        } else {
            refreshUser(oldRefreshToken, request, response);
        }
        filterChain.doFilter(request, response);
    }

    private void validateJWTAndAuthenticate(String accessToken, HttpServletRequest request) throws IllegalAccessException {
        String username = jwtUtil.validateTokenAndRetrieveClaim(accessToken);
        UserDetails userDetails = crosswordUserDetailsService.loadUserByUsername(username);

        String ipAddress = request.getHeader("X-Forwarded-For");
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getRemoteAddr();
        }
        String userAgent = request.getHeader("User-Agent");

        if (refreshTokenService.checkExistingRefreshToken(ipAddress, userAgent, ((CrosswordUserDetails)userDetails).getUser()) == null) {
            throw new IllegalAccessException("There is no such authorized users!");
        }

        UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(userDetails,
                        userDetails.getPassword(),
                        userDetails.getAuthorities());

        if (SecurityContextHolder.getContext().getAuthentication() == null) {
            SecurityContextHolder.getContext().setAuthentication(authToken);
        }
    }

    private void refreshUser(String oldToken, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            if (oldToken != null && !oldToken.isBlank()) {
                String ipAddress = request.getHeader("X-Forwarded-For");
                if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
                    ipAddress = request.getRemoteAddr();
                }
                String userAgent = request.getHeader("User-Agent");
                RefreshToken newRefreshToken;
                newRefreshToken = refreshTokenService.refreshUser(oldToken, ipAddress, userAgent);
                String newAccessToken = jwtUtil.generateAccessToken(newRefreshToken.getUser().getUsername());
                this.setCookies(response, newAccessToken, newRefreshToken.getToken());
                this.validateJWTAndAuthenticate(newAccessToken, request);
            } else {
                AnonymousAuthenticationToken anonymousToken = new AnonymousAuthenticationToken(
                        "anonymous", "anonymousUser", AuthorityUtils.createAuthorityList("ROLE_ANONYMOUS"));
                SecurityContextHolder.getContext().setAuthentication(anonymousToken);
            }
        } catch (ObjectOptimisticLockingFailureException ex) {
            refreshUser(oldToken, request, response);
        } catch (TokenExpiredException | NoSuchElementException | SecurityException | IllegalAccessException e) {
            AnonymousAuthenticationToken anonymousToken = new AnonymousAuthenticationToken(
                    "anonymous", "anonymousUser", AuthorityUtils.createAuthorityList("ROLE_ANONYMOUS"));
            SecurityContextHolder.getContext().setAuthentication(anonymousToken);
            this.setCookies(response, "", "");
        }
    }
    private void setCookies(HttpServletResponse response, String newAccessToken, String newRefreshToken) {
        var accessTokenCookie = new Cookie("access_token", newAccessToken);
        accessTokenCookie.setHttpOnly(true);
        accessTokenCookie.setSecure(true);
        accessTokenCookie.setPath("/");
        var refreshTokenCookie = new Cookie("refresh_token", newRefreshToken);
        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setSecure(true);
        refreshTokenCookie.setPath("/");
        response.addCookie(accessTokenCookie);
        response.addCookie(refreshTokenCookie);
    }
}
