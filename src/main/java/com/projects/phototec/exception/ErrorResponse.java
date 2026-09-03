package com.projects.phototec.exception;

import java.time.Instant;

public record ErrorResponse(
        String message,
        int status,
        Instant timestamp
) {
}
