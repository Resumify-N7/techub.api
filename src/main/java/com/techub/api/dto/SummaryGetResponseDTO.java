package com.techub.api.dto;

public record SummaryGetResponseDTO(
        Long studentId,
        Long summaryId,
        String titulo,
        String conteudo,
        Integer reports,
        Boolean publico,
        Boolean ativo,
        Long totalCurtidas
) {}