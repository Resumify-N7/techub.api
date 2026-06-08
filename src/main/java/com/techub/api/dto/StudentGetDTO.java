package com.techub.api.dto;

import com.techub.api.domain.Avatar;
import com.techub.api.domain.Course;
import jakarta.annotation.Nullable;

public record StudentGetDTO(
        Boolean ativo,
        Long id,
        String nome,
        Integer semestre,
        String bio,
        Avatar avatar,
        Course course,
        Integer pontuacao,
        @Nullable Boolean seguindoCurrentUser,
        @Nullable Boolean seguidoPeloCurrentUser,
        @Nullable Long seguidores,
        @Nullable Long seguindo
) {}
