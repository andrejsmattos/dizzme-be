package com.dizzme.dto;

import java.time.LocalDateTime;

public record ClientDto(
        Long id,
        String name,
        String email,
        String role,
        Boolean active,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        Integer surveysCount
) {}
