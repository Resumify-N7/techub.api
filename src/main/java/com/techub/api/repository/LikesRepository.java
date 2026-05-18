package com.techub.api.repository;

import com.techub.api.domain.Likes;
import com.techub.api.domain.Student;
import com.techub.api.domain.Summary;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LikesRepository extends JpaRepository<Likes, Long> {

    // Verifica se um aluno já curtiu um resumo específico
    // evita curtidas duplicadas
    Optional<Likes> findByStudentAndSummary(Student student, Summary summary);

    // Conta quantas curtidas um resumo tem
    long countBySummary(Summary summary);
}
