package com.techub.api.repository;

import com.techub.api.domain.Course;
import com.techub.api.domain.Student;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;

public interface StudentRepository extends SoftDeleteRepository<Student, Long> {
    Page<Student> findByCourse(Course course, Pageable pageable);
    Page<Student> findByCourseAndSemestre(Course course, Integer semestre, Pageable pageable);
}
