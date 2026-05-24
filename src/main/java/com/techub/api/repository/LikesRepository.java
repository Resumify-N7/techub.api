package com.techub.api.repository;

import com.techub.api.domain.Likes;
import com.techub.api.domain.Student;
import com.techub.api.domain.Summary;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

import java.util.Optional;

public interface LikesRepository extends SoftDeleteRepository<Likes, Long> {

    // Verifica se um aluno já curtiu um resumo específico
    // evita curtidas duplicadas
    Optional<Likes> findByStudentAndSummary(Student student, Summary summary);

    // Conta quantas curtidas um resumo tem
    long countBySummary(Summary summary);

    // Retorna os resumos ordenados pelo número de curtidas (do mais curtido para o menos)
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
}
