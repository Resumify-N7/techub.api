package com.techub.api.repository;

import com.techub.api.domain.Course;

import java.util.List;
import java.util.Optional;

public interface CourseRepository extends SoftDeleteRepository<Course, Long> {
    boolean existsByNameIgnoreCase(String name);
    Optional<Course> findTopByOrderByIdAsc();
    List<Course> findByAtivoFalse();
}