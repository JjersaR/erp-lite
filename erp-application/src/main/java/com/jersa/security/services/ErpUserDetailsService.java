package com.jersa.security.services;

import com.jersa.security.repositories.UserCredentialsProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@Primary
@RequiredArgsConstructor
public class ErpUserDetailsService implements UserDetailsService {
    private final UserCredentialsProvider repository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return this.repository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("Username not found"));
    }
}