package com.techub.api.repository;

import com.techub.api.domain.Summary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SummaryRepository extends SoftDeleteRepository<Summary, Long>, JpaSpecificationExecutor<Summary> {
    @EntityGraph(attributePaths = {"student", "student.avatar", "subject"})
    Page<Summary> findActive(Pageable pageable);

    List<Summary> findByStudentId(Long studentId);
    List<Summary> findByStudentIdAndAtivoTrue(Long studentId);
    Page<Summary> findBySubjectIdAndAtivoTrue(Long subjectId, Pageable pageable);

    @EntityGraph(attributePaths = {"student", "student.avatar", "subject"})
    Page<Summary> findByStudentIdAndAtivoTrue(Long studentId, Pageable pageable);


    @Query("""
        SELECT s FROM Summary s
        WHERE s.student.id IN :followingUsers
           OR s.subject.course.id IN :followingCourses
        ORDER BY s.datahora DESC
    """)
    @EntityGraph(attributePaths = {"student", "student.avatar", "subject"})
    Page<Summary> findFeedSummaries(
            List<Long> followingUsers,
            List<Long> followingCourses,
            Pageable pageable
    );
    @Query("""
        SELECT DISTINCT s FROM Summary s
        LEFT JOIN s.tags t
                WHERE (:universityId IS NULL OR s.subject.course.university.id = :universityId)
                    AND (:courseId     IS NULL OR s.subject.course.id = :courseId)
          AND (:tagId        IS NULL OR t.id = :tagId)
          AND (:semestre     IS NULL OR s.subject.semestre = :semestre)
        ORDER BY s.datahora DESC
    """)
    Page<Summary> findByFilters(
            @Param("universityId") Long universityId,
            @Param("courseId")     Long courseId,
            @Param("tagId")        Long tagId,
            @Param("semestre")     Integer semestre,
            Pageable pageable
    );
    List<Summary> findByPublicoTrue();
}
