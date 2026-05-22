package com.techub.api.dto;

import jakarta.validation.constraints.NotBlank;

public record UserUpdateStudentRequestDTO(
       String nome,
       Integer semestre,
       String bio,
       String avatarUrl
) {}
