package com.techub.api.dto;

public record SummaryGetResponseDTO(
        Long studentId,
        Long summaryId,
        Long subjectId,
        String subjectNome,
        String titulo,
        String conteudo,
        Integer reports,
        Boolean publico,
        Boolean ativo,
        Long totalCurtidas
) {}