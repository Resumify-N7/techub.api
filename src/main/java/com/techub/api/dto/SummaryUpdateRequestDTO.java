package com.techub.api.dto;

import com.fasterxml.jackson.annotation.JsonAlias;

import java.util.List;

public record SummaryUpdateRequestDTO(
        String titulo,
        String conteudo,
        Long summaryId,
        Boolean publico,
        @JsonAlias({"tags_ids", "tags_id"}) List<Long> tags
)  {}
