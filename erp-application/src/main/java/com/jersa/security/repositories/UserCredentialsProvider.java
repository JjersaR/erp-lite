package com.jersa.security.repositories;

import com.jersa.security.dtos.AppUserDetails;

import java.util.Optional;

public interface UserCredentialsProvider {
    Optional<AppUserDetails> findByUsername(String username);
}
