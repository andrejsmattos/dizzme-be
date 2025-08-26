package com.dizzme.dto;

import java.time.LocalDateTime;
import java.util.List;

public record ResponseDto(
        Long id,
        LocalDateTime submittedAt,
        String userIp,
        List<AnswerDto> answers
) {}