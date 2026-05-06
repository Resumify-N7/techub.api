package com.techub.api.repository;

import com.techub.api.domain.Course;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CourseRepository extends JpaRepository<Course, Long> {
    boolean existsByNameIgnoreCase(String name);
    Optional<Course> findTopByOrderByIdAsc();
}