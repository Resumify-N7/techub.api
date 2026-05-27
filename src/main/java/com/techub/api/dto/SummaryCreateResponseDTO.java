package com.techub.api.dto;

public record SummaryCreateResponseDTO(
        Long studentId,
        Long subjectId,
        String titulo,
        String conteudo
)  {}
