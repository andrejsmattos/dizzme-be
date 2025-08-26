package com.dizzme.dto;

import java.time.LocalDateTime;

public record SurveySummaryDto(
        Long id,
        String title,
        String description,
        Boolean active,
        String status,
        LocalDateTime createdAt,
        Integer questionsCount,
        Integer responsesCount,
        String publicUrl
) {}
