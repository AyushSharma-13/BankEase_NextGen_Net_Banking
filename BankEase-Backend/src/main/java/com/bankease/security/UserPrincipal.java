package com.bankease.security;

import com.bankease.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

@Getter
@AllArgsConstructor
public class UserPrincipal implements UserDetails {

    private final Long userId;
    private final Long customerId;
    private final String username;
    private final String password;
    private final String role;
    private final Collection<? extends GrantedAuthority> authorities;
    private final boolean enabled;

    public static UserPrincipal create(User user) {
        SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + user.getRole().name());
        Long customerId = (user.getCustomer() != null) ? user.getCustomer().getCustomerId() : null;

        return new UserPrincipal(
                user.getUserId(),
                customerId,
                user.getUsername(),
                user.getPasswordHash(),
                user.getRole().name(),
                Collections.singletonList(authority),
                Boolean.TRUE.equals(user.getEnabled())
        );
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }
}
