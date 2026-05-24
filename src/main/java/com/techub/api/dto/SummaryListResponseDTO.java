package com.techub.api.dto;

public record SummaryListResponseDTO(
        Long studentId,
        String studentNome,
        String studentUrl,
        Long summaryId,
        String titulo,
        String conteudo,
        Integer reports,
        Boolean publico,
        Boolean ativo,
        Long totalCurtidas
) {}