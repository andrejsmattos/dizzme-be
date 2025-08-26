package com.dizzme.dto;

public record QuestionTemplate(
        String type,
        String text,
        String options,
        Boolean required,
        Integer questionOrder
) {}