package com.jersa.security.repositories.impl;

import com.jersa.security.dtos.AccountStatus;
import com.jersa.security.dtos.AppRole;
import com.jersa.security.dtos.AppUserDetails;
import com.jersa.security.repositories.UserCredentialsProvider;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Repository
public class InMemoryUserCredentialsProvider implements UserCredentialsProvider {

    private static final Map<String, AppUserDetails> USERS = Map.of(

            "admin", new AppUserDetails(
                    "admin",
                    // {bcrypt} — Spring Security 7 requiere password encoding
                    "$2a$10$1wo8J65mXaNzlfbWYvcl5OOc33eYC6mj8Zk8/Y438L79XToOTqDQC",
                    AccountStatus.ACTIVE,
                    Set.of(AppRole.admin())
            ),

            "manager", new AppUserDetails(
                    "manager",
                    "$2a$10$1wo8J65mXaNzlfbWYvcl5OOc33eYC6mj8Zk8/Y438L79XToOTqDQC",
                    AccountStatus.ACTIVE,
                    Set.of(AppRole.user())   // manager = USER role en tu tabla
            ),

            "employee", new AppUserDetails(
                    "employee",
                    "$2a$10$1wo8J65mXaNzlfbWYvcl5OOc33eYC6mj8Zk8/Y438L79XToOTqDQC",
                    AccountStatus.ACTIVE,
                    Set.of(AppRole.user())
            )
    );

    @Override
    public Optional<AppUserDetails> findByUsername(String username) {
        return Optional.ofNullable(USERS.get(username));
    }
}