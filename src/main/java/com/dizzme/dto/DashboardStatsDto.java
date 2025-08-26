package com.dizzme.dto;

public record DashboardStatsDto(
        Integer totalSurveys,
        Integer activeSurveys,
        Integer totalResponses,
        Integer responsesToday,
        Double avgNPS,
        Double avgCSAT,
        Double avgCES
) {}
