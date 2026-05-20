package com.jersa.security.repositories.impl;

import com.jersa.security.repositories.RefreshTokenStore;
import org.springframework.stereotype.Repository;

import java.util.concurrent.ConcurrentHashMap;

@Repository
public class InMemoryRefreshTokenStore implements RefreshTokenStore {

    // ConcurrentHashMap para thread safety
    private final ConcurrentHashMap<String, String> store = new ConcurrentHashMap<>();

    @Override
    public void save(String username, String refreshToken) {
        store.put(username, refreshToken);
    }

    @Override
    public boolean exists(String username, String refreshToken) {
        // verifica que el token del store coincida exactamente
        return refreshToken.equals(store.get(username));
    }

    @Override
    public void delete(String username) {
        store.remove(username);
    }
}
