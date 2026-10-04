package com.backend.crosswords.admin.services;

import com.backend.crosswords.admin.models.CrosswordUserDetails;
import com.backend.crosswords.admin.models.User;
import com.backend.crosswords.admin.repositories.UserRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class CrosswordUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    public CrosswordUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> user = userRepository.findByUsernameOrEmail(username, username);
        if (user.isEmpty()) {
            throw new UsernameNotFoundException("User not found");
        }
        return new CrosswordUserDetails(this, user.get());
    }

    public List<SimpleGrantedAuthority> getAuthorities(User user) {
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        if (user.getRole() == null || user.getRole().getAuthorities() == null || user.getRole().getAuthorities().length == 0) {
            return Collections.singletonList(new SimpleGrantedAuthority("NO_AUTHORITIES"));
        }
        var authoritiesEnum = user.getRole().getAuthorities();
        for (var authority : authoritiesEnum) {
            authorities.add(new SimpleGrantedAuthority(authority.name()));
        }
        return authorities;
    }
}
