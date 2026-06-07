package com.techub.api.repository;

import com.techub.api.domain.Course;
import com.techub.api.domain.Student;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.Query;

public interface StudentRepository extends SoftDeleteRepository<Student, Long> {
    Page<Student> findByCourse(Course course, Pageable pageable);
    Page<Student> findByCourseAndSemestre(Course course, Integer semestre, Pageable pageable);
    long countByAtivoTrue();
    long countByAtivoFalse();

    @Query("""
        SELECT COUNT(f)
        FROM Student s
        JOIN s.followers f
        WHERE s.id = :studentId
    """)
    Long countFollowers(Long studentId);

    @Query("""
        SELECT COUNT(f)
        FROM Student s
        JOIN s.following f
        WHERE s.id = :studentId
    """)
    Long countFollowing(Long studentId);
}
