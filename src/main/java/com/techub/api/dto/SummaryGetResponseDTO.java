package com.techub.api.dto;

public record SummaryGetResponseDTO(
        Long studentId,
        Long summaryId,
        String titulo,
        String conteudo
) {}
