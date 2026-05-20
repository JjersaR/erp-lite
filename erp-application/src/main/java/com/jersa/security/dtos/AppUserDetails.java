package com.jersa.security.dtos;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

public record AppUserDetails(
        String username,
        String password,
        AccountStatus accountStatus,
        Set<AppRole> roles
) implements UserDetails {

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority(role.authority()))
                .collect(Collectors.toSet());
    }

    @Override
    public boolean isEnabled() {
        return accountStatus == AccountStatus.ACTIVE;
    }

    @Override
    public boolean isAccountNonLocked() {
        return accountStatus != AccountStatus.LOCKED;
    }

    @Override
    public boolean isAccountNonExpired() {
        return accountStatus != AccountStatus.EXPIRED;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return accountStatus != AccountStatus.CREDENTIALS_EXPIRED;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public String getPassword() {
        return password;
    }
}