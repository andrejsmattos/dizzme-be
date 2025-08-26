package com.dizzme.dto;

import java.time.LocalDateTime;

public record ClientSummaryDto(
        Long id,
        String name,
        String email,
        Boolean active,
        LocalDateTime createdAt,
        Integer surveysCount,
        Integer responsesCount
) {}
