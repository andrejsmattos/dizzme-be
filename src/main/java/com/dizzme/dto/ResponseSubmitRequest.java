package com.dizzme.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ResponseSubmitRequest(
        @NotBlank(message = "ID público da pesquisa é obrigatório")
        String surveyPublicId,

        @NotNull(message = "Respostas são obrigatórias")
        @Size(min = 1, message = "Deve conter pelo menos uma resposta")
        @Valid
        List<AnswerSubmitRequest> answers,

        Boolean lgpdConsent
) {}
