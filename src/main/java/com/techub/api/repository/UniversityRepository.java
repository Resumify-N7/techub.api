package com.techub.api.repository;

import com.techub.api.domain.University;
import java.util.Optional;

public interface UniversityRepository extends SoftDeleteRepository<University, Long> {
    boolean existsByNameIgnoreCase(String name);
    Optional<University> findTopByOrderByIdAsc();
    Optional<University> findByNameIgnoreCase(String fatecItaquera);
    long countByAtivoTrue();
    long countByAtivoFalse();
}
