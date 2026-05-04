package com.techub.api.repository;

import com.techub.api.domain.CourseChange;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;

public interface CourseChangeRepository extends JpaRepository<CourseChange, Long> {
    int countByStudentIdAndDataTrocaAfter(Long studentId, LocalDateTime dataTroca);
}
