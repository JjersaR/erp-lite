package com.jersa.security.services;

import com.jersa.security.dtos.AppUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Objects;

import static com.jersa.security.utils.SecurityRouterConstants.*;

@Service
@RequiredArgsConstructor
public class JWTService {
    private final JwtEncoder jwtEncoder;
    private final JwtDecoder jwtDecoder;

    @Value("${app.security.expiration:86400}")
    private long expirationTime;

    // 7 días en segundos para el refresh token
    @Value("${app.security.refresh-expiration:604800}")
    private long refreshExpirationTime;

    public String generateToken(AppUserDetails details) {
        Instant now = Instant.now();
        List<String> roles = details.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        JwtClaimsSet claimsSet = JwtClaimsSet.builder()
                .subject(details.username())
                .issuedAt(now)
                .expiresAt(now.plus(expirationTime, ChronoUnit.SECONDS))
                .claim("roles", roles)
                .claim("enabled", details.isEnabled())
                .build();

        return this.jwtEncoder.encode(JwtEncoderParameters.from(claimsSet)).getTokenValue();
    }

    // genera un refresh token con mayor expiración y sin roles
    public String generateRefreshToken(AppUserDetails userDetails) {
        Instant now = Instant.now();

        JwtClaimsSet claimsSet = JwtClaimsSet.builder()
                .subject(userDetails.getUsername())
                .issuedAt(now)
                .expiresAt(now.plus(refreshExpirationTime, ChronoUnit.SECONDS))
                .claims(map -> {
                    map.put(TOKEN_TYPE_CLAIM, REFRESH_TOKEN_TYPE); // marca como refresh token
                })
                .build();

        return this.jwtEncoder.encode(JwtEncoderParameters.from(claimsSet))
                .getTokenValue();
    }

    public boolean isTokenValid(String token) {
        try {
            Jwt decoded = this.jwtDecoder.decode(token);

            return Instant.now().isBefore(Objects.requireNonNull(decoded.getExpiresAt()));
        } catch (Exception e) {
            return false;
        }
    }

    // valida que el token no expiró y es de tipo refresh
    public boolean isRefreshTokenValid(String token) {
        try {
            Jwt decodedJwt = this.jwtDecoder.decode(token);
            boolean notExpired = Instant.now()
                    .isBefore(Objects.requireNonNull(decodedJwt.getExpiresAt()));
            // verifica que sea refresh token y no un access token reutilizado
            boolean isRefreshType = REFRESH_TOKEN_TYPE
                    .equals(decodedJwt.getClaim(TOKEN_TYPE_CLAIM));
            return notExpired && isRefreshType;
        } catch (Exception e) {
            return false;
        }
    }

    public List<String> extractRoles(String token) {
        return this.jwtDecoder.decode(token).getClaim("roles");
    }

    public String extractUsername(String token) {
        return this.jwtDecoder.decode(token).getSubject();
    }
}