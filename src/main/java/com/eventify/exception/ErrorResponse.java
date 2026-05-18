package com.eventify.exception;

import java.time.LocalDateTime;

// Standard error payload returned by the API for any handled exception
public record ErrorResponse(
        LocalDateTime timestamp,
        int status,
        String error,
        String message,
        String path
) {}