package com.dizzme.dto;

import java.util.List;

public record SurveyTemplate(
        String name,
        String description,
        String category,
        List<QuestionTemplate> questions
) {}
