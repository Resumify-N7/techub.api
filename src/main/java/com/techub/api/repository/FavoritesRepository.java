package com.techub.api.repository;

import com.techub.api.domain.Favorites;
import com.techub.api.domain.Student;
import com.techub.api.domain.Summary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;

import java.util.List;
import java.util.Optional;

public interface FavoritesRepository extends SoftDeleteRepository<Favorites, Long> {

    Optional<Favorites> findByStudentAndSummary(Student student, Summary summary);

    List<Favorites> findByStudentId(Long studentId);

    @EntityGraph(attributePaths = {"summary", "summary.student", "summary.student.avatar", "summary.subject"})
    Page<Favorites> findByStudentIdAndAtivoTrueOrderByIdDesc(Long studentId, Pageable pageable);
}