package com.jersa.security.repositories;

public interface RefreshTokenStore {
    void save(String username, String refreshToken);

    boolean exists(String username, String refreshToken);

    void delete(String username);
}
