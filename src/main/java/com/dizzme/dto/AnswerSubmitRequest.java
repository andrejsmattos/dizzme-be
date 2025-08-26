package com.dizzme.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@JsonIgnoreProperties(ignoreUnknown = true)
public record AnswerSubmitRequest(
        @NotNull(message = "ID da pergunta é obrigatório")
        Long questionId,

        @NotBlank(message = "Valor da resposta é obrigatório")
        String value
) {}
