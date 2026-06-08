package com.techub.api.dto;

import com.techub.api.domain.Avatar;
import com.techub.api.domain.Subject;

public record ProfessorGetDTO(
        Long id,
        String nome,
        String bio,
        Avatar avatar,
        Subject subject,
        String email
) {}