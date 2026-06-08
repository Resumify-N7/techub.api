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
        SELECT s.id, s.student.id, s.student.nome, a.url, s.titulo, s.conteudo, s.reports, s.publico, s.ativo, COUNT(l)
        FROM Likes l
        JOIN l.summary s
        LEFT JOIN s.student st
        LEFT JOIN st.avatar a
        GROUP BY s.id, s.student.id, s.student.nome, a.url, s.titulo, s.conteudo, s.reports, s.publico, s.ativo
        ORDER BY COUNT(l) DESC
    """)
    List<Object[]> findRanking();

    Page<Likes> findByStudentIdOrderByIdDesc(Long studentId, Pageable pageable);
}
