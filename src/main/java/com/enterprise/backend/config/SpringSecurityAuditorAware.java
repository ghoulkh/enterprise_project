package com.enterprise.backend.config;

import lombok.NonNull;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetails;

import java.util.Optional;

public class SpringSecurityAuditorAware implements AuditorAware<String> {

    @NonNull
    public Optional<String> getCurrentAuditor() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return Optional.empty();
        }

        try {
            return Optional.ofNullable(((UserDetails) authentication.getPrincipal()).getUsername());
        } catch (Exception e) {
            return Optional.ofNullable(((WebAuthenticationDetails) authentication.getDetails()).getRemoteAddress());
        }
    }
}