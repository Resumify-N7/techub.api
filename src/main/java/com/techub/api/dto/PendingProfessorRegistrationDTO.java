package com.techub.api.dto;

import com.techub.api.email.ValidInstituicaoEmail;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record PendingProfessorRegistrationDTO(
        @NotBlank String nome,

        @Email
        @ValidInstituicaoEmail
        @NotBlank String email,

        @NotBlank String senha,

        @NotNull(message = "Matéria é obrigatória")
        @Positive(message = "Selecione uma matéria válida")
        Long subjectId
) {}