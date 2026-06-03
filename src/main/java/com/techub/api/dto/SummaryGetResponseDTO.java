package com.techub.api.dto;

import java.util.List;

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
        Long totalCurtidas,
        List<String> tags
) {}