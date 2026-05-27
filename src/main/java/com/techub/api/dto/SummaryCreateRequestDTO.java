package com.techub.api.dto;

public record SummaryCreateRequestDTO(
        String titulo,
        String conteudo,
        Long subjectId
) {}
