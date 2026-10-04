package com.backend.crosswords.admin.models;

import com.backend.crosswords.admin.services.CrosswordUserDetailsService;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

public class CrosswordUserDetails implements UserDetails {
    private final CrosswordUserDetailsService crosswordUserDetailsService;
    private final User user;

    public CrosswordUserDetails(CrosswordUserDetailsService crosswordUserDetailsService, User user) {
        this.crosswordUserDetailsService = crosswordUserDetailsService;
        this.user = user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return crosswordUserDetailsService.getAuthorities(this.user);
    }

    @Override
    public String getPassword() {
        return this.user.getPassword();
    }

    @Override
    public String getUsername() {
        if (user.getUsername() == null || user.getUsername().isEmpty()) {
            return user.getEmail();
        }
        return user.getUsername();
    }

    public User getUser() {
        return user;
    }
}
