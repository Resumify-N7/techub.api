package com.techub.api.dto;

import com.fasterxml.jackson.annotation.JsonAlias;

import java.util.List;

public record SummaryCreateRequestDTO(
        String titulo,
        String conteudo,
        Long subjectId,
        Boolean publico,
        @JsonAlias({"tags_ids", "tags_id"}) List<Long> tagsIds
) {}
