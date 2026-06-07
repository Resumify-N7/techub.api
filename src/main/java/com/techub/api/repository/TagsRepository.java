package com.techub.api.repository;

import com.techub.api.domain.Tags;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TagsRepository extends SoftDeleteRepository<Tags, Long> {
    boolean existsByNameIgnoreCase(String name);
    boolean existsByNameIgnoreCaseAndIdNot(String name, Long id);
    Page<Tags> findByAtivoTrue(Pageable pageable);
    Page<Tags> findByAtivoFalse(Pageable pageable);
    long countByAtivoTrue();
    long countByAtivoFalse();
}
