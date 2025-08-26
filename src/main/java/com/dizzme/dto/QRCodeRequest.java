package com.dizzme.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record QRCodeRequest(
        @NotBlank(message = "URL é obrigatória")
        String url,

        @Min(value = 100, message = "Tamanho mínimo: 100px")
        @Max(value = 1000, message = "Tamanho máximo: 1000px")
        Integer size
) {}