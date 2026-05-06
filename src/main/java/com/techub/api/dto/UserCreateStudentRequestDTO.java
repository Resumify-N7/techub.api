package com.techub.api.dto;

import jakarta.validation.constraints.NotBlank;

public record UserCreateStudentRequestDTO(
       @NotBlank String nome,
       @NotBlank String email,
       @NotBlank String senha,
       Integer semestre,
       String bio,
       String foto
) {}
