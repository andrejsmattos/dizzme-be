package com.dizzme.dto;

public record ValidationErrorResponse(
        String field,
        String message,
        Object rejectedValue
) {}
