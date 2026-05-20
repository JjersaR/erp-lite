package com.jersa.dtos;

public record AuthResponse(String jwt, String refreshToken) {
}
