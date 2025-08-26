package com.dizzme.dto;

import java.time.LocalDateTime;
import java.util.List;

public record SurveyStatsDto(
        Long surveyId,
        String surveyTitle,
        Integer totalResponses,
        LocalDateTime lastResponse,
        List<QuestionStatsDto> questionStats
) {}