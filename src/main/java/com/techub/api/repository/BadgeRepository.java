package com.techub.api.repository;

import com.techub.api.domain.Badge;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface BadgeRepository extends SoftDeleteRepository<Badge, Long> {
    boolean existsByNameIgnoreCase(String name);
    boolean existsByNameIgnoreCaseAndIdNot(String name, Long id);
    Optional<Badge> findByNameIgnoreCaseAndAtivoTrue(String name);
    Page<Badge> findByAtivoTrue(Pageable pageable);
    Page<Badge> findByAtivoFalse(Pageable pageable);
    long countByAtivoTrue();
    long countByAtivoFalse();
}