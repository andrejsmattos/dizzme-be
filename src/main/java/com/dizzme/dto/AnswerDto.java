package com.dizzme.dto;

public record AnswerDto(
        Long id,
        Long questionId,
        String questionText,
        String questionType,
        String value
) {}