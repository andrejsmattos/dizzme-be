package com.dizzme.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record SurveyUpdateRequest(
        String title,

        String description,

        Boolean active,

        String status,

        @Valid
        List<QuestionCreateRequest> questions
) {}