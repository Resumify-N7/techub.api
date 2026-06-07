package com.techub.api.repository;

import com.techub.api.domain.Course;
import com.techub.api.domain.Subject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;

public interface SubjectRepository extends SoftDeleteRepository<Subject, Long> {
    boolean existsByNameIgnoreCase(String name);
    boolean existsByNameIgnoreCaseAndIdNot(String name, Long id);
    List<Subject> findByCourse(Course course);
    List<Subject> findByCourseAndAtivoTrue(Course course);
    Page<Subject> findByCourseAndSemestre(Course course, Integer semestre, Pageable pageable);
    Page<Subject> findByCourseAndSemestreAndAtivoTrue(Course course, Integer semestre, Pageable pageable);
    long countByAtivoTrue();
    long countByAtivoFalse();
}
