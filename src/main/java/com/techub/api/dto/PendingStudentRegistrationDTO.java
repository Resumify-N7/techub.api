package com.techub.api.dto;

import com.techub.api.email.ValidInstituicaoEmail;
import jakarta.validation.constraints.Email;

public record PendingStudentRegistrationDTO(
        String nome,

        @Email
        @ValidInstituicaoEmail
        String email,
        String senha,
        Integer semestre
) {}