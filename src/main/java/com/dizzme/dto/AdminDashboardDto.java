package com.dizzme.dto;

import java.util.List;
import java.util.Map;

public record AdminDashboardDto(
        Integer totalClients,
        Integer activeClients,
        Integer totalSurveys,
        Integer totalResponses,
        Map<String, Integer> monthlyStats,
        List<ClientSummaryDto> recentClients,
        List<SurveySummaryDto> recentSurveys
) {}