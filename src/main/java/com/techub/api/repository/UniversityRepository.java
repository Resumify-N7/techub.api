package com.techub.api.repository;

import com.techub.api.domain.University;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UniversityRepository extends JpaRepository<University, Long> {
    boolean existsByNameIgnoreCase(String name);
    Optional<University> findTopByOrderByIdAsc();
}
