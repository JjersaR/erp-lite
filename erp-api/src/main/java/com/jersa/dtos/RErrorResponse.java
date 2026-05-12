package com.jersa.dtos;

import org.springframework.http.HttpStatus;

import java.time.Instant;

public record RErrorResponse(
        Instant timestamp,
        int status,
        String error,
        String message,
        String path
) {
    public static RErrorResponse of(HttpStatus status, String error, String message, String path) {
        return new RErrorResponse(
                Instant.now(),
                status.value(),
                error,
                message,
                path
        );
    }
}
