package com.techub.api.dto;

import java.util.List;

public record SummaryListResponseDTO(
        Long studentId,
        String studentNome,
        String studentUrl,
        Long subjectId,
        String subjectNome,
        Long summaryId,
        String titulo,
        String conteudo,
        Integer reports,
        Boolean publico,
        Boolean ativo,
        Long totalCurtidas,
        List<String> tags
) {}