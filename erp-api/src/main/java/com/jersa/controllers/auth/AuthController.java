package com.jersa.controllers.auth;

import com.jersa.dtos.AuthRequest;
import com.jersa.dtos.AuthResponse;
import com.jersa.dtos.RefreshRequest;
import com.jersa.security.dtos.AppUserDetails;
import com.jersa.security.repositories.RefreshTokenStore;
import com.jersa.security.repositories.UserCredentialsProvider;
import com.jersa.security.services.JWTService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final JWTService jwtService;
    private final RefreshTokenStore refreshTokenStore;
    private final UserCredentialsProvider userCredentialsProvider;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest request) {
        final var authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.username(), request.password()));

        final var userDetails = (AppUserDetails) authentication.getPrincipal();

        assert userDetails != null;

        final var token = jwtService.generateToken(userDetails);
        var refreshToken = jwtService.generateRefreshToken(userDetails);

        refreshTokenStore.save(userDetails.getUsername(), refreshToken);

        return ResponseEntity.ok(new AuthResponse(token, refreshToken));
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(@RequestBody RefreshRequest request) {
        var refreshToken = request.refreshToken();

        // valida firma, expiración y tipo del token
        if (!jwtService.isRefreshTokenValid(refreshToken)) {
            return ResponseEntity.status(401).build();
        }

        var username = jwtService.extractUsername(refreshToken);

        // verifica que el token coincida con el almacenado — detecta reutilización
        if (!refreshTokenStore.exists(username, refreshToken)) {
            refreshTokenStore.delete(username); // invalida toda la sesión si hay reutilización
            return ResponseEntity.status(401).build();
        }

        var userDetails = (AppUserDetails) userCredentialsProvider
                .findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        var newAccessToken = jwtService.generateToken(userDetails);
        var newRefreshToken = jwtService.generateRefreshToken(userDetails); // rotation obligatoria OAuth 2.1

        // reemplaza el refresh token anterior — el viejo queda invalidado
        refreshTokenStore.save(username, newRefreshToken);

        return ResponseEntity.ok(new AuthResponse(newAccessToken, newRefreshToken));
    }
}