package com.dizzme.dto;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record ExportRequest(
        @NotNull(message = "ID da pesquisa é obrigatório")
        Long surveyId,

        String format, // CSV or XLSX

        LocalDateTime dateFrom,

        LocalDateTime dateTo,

        Boolean includeHeaders
) {}