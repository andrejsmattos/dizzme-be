package com.dizzme.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record ClientUpdateRequest(
        @NotBlank(message = "Nome é obrigatório")
        String name,

        @Email(message = "Email deve ser válido")
        String email,

        Boolean active
) {}
