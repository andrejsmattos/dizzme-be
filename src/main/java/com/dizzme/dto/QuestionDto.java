package com.dizzme.dto;

import java.util.List;

public record QuestionDto(
        Long id,
        String type,
        String text,
        String options,
        Integer questionOrder,
        Boolean required,
        List<String> parsedOptions
) {}
