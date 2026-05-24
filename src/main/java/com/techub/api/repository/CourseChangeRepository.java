package com.techub.api.repository;

import com.techub.api.domain.CourseChange;

import java.time.LocalDateTime;

public interface CourseChangeRepository extends SoftDeleteRepository<CourseChange, Long> {
    int countByStudentIdAndDataTrocaAfter(Long studentId, LocalDateTime dataTroca);
}
