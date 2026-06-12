package com.techub.api.dto;

import com.techub.api.domain.Course;

public record FollowGetResponseDTO(
        Long studentId,
        Long followerId,
        String name,
        Integer semestre,
        Course course,
        Integer seguidores,
        String studentUrl
) {}
