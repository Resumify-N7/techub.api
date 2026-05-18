package com.techub.api.dto;

import com.techub.api.domain.Course;

public record FollowesGetResponseDTO(
        Long studentId,
        Long followerd,
        String name,
        Integer semestre,
        Course course
) {}
