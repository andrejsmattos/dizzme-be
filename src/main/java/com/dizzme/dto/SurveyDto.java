package com.dizzme.dto;

import java.time.LocalDateTime;
import java.util.List;

public record SurveyDto(
        Long id,
        String title,
        String description,
        String publicId,
        Boolean active,
        String status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        List<QuestionDto> questions,
        Integer responsesCount,
        String publicUrl,
        String qrCodeUrl
) {}