package com.dizzme.dto;

import java.time.LocalDateTime;

public record MetricsDto(
        String metric,
        String period,
        Object value,
        String unit,
        LocalDateTime calculatedAt
) {}