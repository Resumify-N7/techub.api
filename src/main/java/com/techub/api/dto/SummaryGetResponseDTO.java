package com.techub.api.dto;

import java.util.List;

public record SummaryGetResponseDTO(
        Long summaryId,
        Long studentId,
        String studentNome,
        String studentUrl,
        Long subjectId,
        String subjectNome,
        String titulo,
        String conteudo,
        Integer reports,
        Boolean publico,
        Boolean ativo,
        Long totalCurtidas,
        List<TagResponseDTO> tags
) {}