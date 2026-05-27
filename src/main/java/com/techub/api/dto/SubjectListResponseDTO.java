package com.techub.api.dto;

public record SubjectListResponseDTO(
        Long id,
        String name,
        Integer semestre,
        Long courseId,
        String courseName,
        Boolean ativo
) {}