package com.techub.api.repository;

import com.techub.api.domain.Course;
import com.techub.api.domain.Subject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubjectRepository extends JpaRepository<Subject, Long> {
    boolean existsByNameIgnoreCase(String name);
    java.util.List<Subject> findByCourse(Course course);
    Page<Subject> findByCourseAndSemestre(Course course, Integer semestre, Pageable pageable);
}
