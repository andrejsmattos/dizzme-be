package com.dizzme.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List; // <-- ADICIONE ESTA LINHA

@JsonIgnoreProperties(ignoreUnknown = true)
public record QuestionCreateRequest(
        @NotBlank(message = "Tipo da pergunta é obrigatório")
        String type,

        @NotBlank(message = "Texto da pergunta é obrigatório")
        String text,

        // Agora o Jackson espera um array de strings, que será convertido para uma List<String>
        List<String> options,

        @NotNull(message = "Ordem é obrigatória")
        @Min(value = 1, message = "Ordem deve ser maior que 0")
        Integer questionOrder,

        Boolean required
) {}
