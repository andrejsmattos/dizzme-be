package com.dizzme.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@JsonIgnoreProperties(ignoreUnknown = true)
public record QuestionCreateRequest(
        @NotBlank(message = "Tipo da pergunta é obrigatório")
        String type,

        @NotBlank(message = "Texto da pergunta é obrigatório")
        String text,

        String options, // JSON string for options

        @NotNull(message = "Ordem é obrigatória")
        @Min(value = 1, message = "Ordem deve ser maior que 0")
        Integer questionOrder,

        Boolean required
) {}
