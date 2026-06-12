package com.techub.api.repository;

import com.techub.api.domain.Summary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SummaryRepository extends SoftDeleteRepository<Summary, Long>, JpaSpecificationExecutor<Summary> {

    @EntityGraph(attributePaths = {"student", "student.avatar", "subject", "tagLinks.tag"})
    Page<Summary> findActive(Pageable pageable);

    Page<Summary> findBySubjectIdAndAtivoTrue(Long subjectId, Pageable pageable);

    @EntityGraph(attributePaths = {"student", "student.avatar", "subject", "tagLinks.tag"})
    Page<Summary> findByStudentIdAndAtivoTrue(Long studentId, Pageable pageable);

    long countByAtivoTrue();
    long countByAtivoFalse();

    @EntityGraph(attributePaths = {"student", "student.avatar", "subject", "tagLinks.tag"})
    @Query("""
        SELECT s FROM Summary s
        WHERE s.student.id IN :followingUsers
        ORDER BY s.datahora DESC
    """)
    Page<Summary> findFeedSummaries(
            @Param("followingUsers") List<Long> followingUsers,
            Pageable pageable
    );

    @Query("""
        SELECT s
        FROM Summary s
        WHERE s.ativo = true AND s.publico = true
        ORDER BY (
            SELECT COUNT(l)
            FROM Likes l
            WHERE l.summary = s
        ) DESC,
        s.datahora DESC
    """)
    Page<Summary> findRanking(Pageable pageable);

    List<Summary> findByPublicoTrue();
}