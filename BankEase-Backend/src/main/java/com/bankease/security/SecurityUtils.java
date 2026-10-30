package com.bankease.security;

import com.bankease.exception.UnauthorizedAccessException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public final class SecurityUtils {

    private SecurityUtils() {}

    public static UserPrincipal getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || !(authentication.getPrincipal() instanceof UserPrincipal)) {
            throw new UnauthorizedAccessException("User is not authenticated");
        }
        return (UserPrincipal) authentication.getPrincipal();
    }

    public static Long getCurrentCustomerId() {
        UserPrincipal user = getCurrentUser();
        if (user.getCustomerId() == null) {
            throw new UnauthorizedAccessException("Current user does not have an associated customer account");
        }
        return user.getCustomerId();
    }

    public static String getCurrentUsername() {
        return getCurrentUser().getUsername();
    }
}
