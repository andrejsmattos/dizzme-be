package com.dizzme.dto;

public record AuthResponse(
        String token,
        String type,
        Long id,
        String name,
        String email,
        String role
) {}
