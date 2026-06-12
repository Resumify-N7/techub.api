package com.techub.api.repository;

import com.techub.api.domain.Likes;
import com.techub.api.domain.Student;
import com.techub.api.domain.Summary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

import java.util.Optional;

public interface LikesRepository extends SoftDeleteRepository<Likes, Long> {

    Optional<Likes> findByStudentAndSummary(Student student, Summary summary);

    long countBySummary(Summary summary);

    @Query("""
    SELECT l.summary.id, COUNT(l)
    FROM Likes l
    WHERE l.summary.ativo = true AND l.summary.publico = true
    GROUP BY l.summary.id
    ORDER BY COUNT(l) DESC
""")
    List<Object[]> findRanking();

    Page<Likes> findByStudentIdOrderByIdDesc(Long studentId, Pageable pageable);
}
