package com.dizzme.dto;

import java.util.List;
import java.util.Map;

public record QuestionStatsDto(
        Long questionId,
        String questionText,
        String questionType,
        Integer totalAnswers,
        Map<String, Integer> answerDistribution,
        List<String> textAnswers,
        Double average, // For numeric questions
        Integer npsPromoters, // For NPS
        Integer npsPassives,
        Integer npsDetractors,
        Double npsScore
) {}
