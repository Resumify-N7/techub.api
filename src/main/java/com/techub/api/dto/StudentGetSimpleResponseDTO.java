package com.techub.api.dto;

public record StudentGetSimpleResponseDTO(
        Long studentId,
        String nome,
        Integer semestre,
        String url,
        Integer qtdSeguidores
) {}
