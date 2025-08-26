package com.dizzme.dto;

import java.time.LocalDateTime;
import java.util.Map;

public record ErrorResponse(
        String error,
        String message,
        String path,
        int status,
        LocalDateTime timestamp,
        Map<String, String> validationErrors
) {}