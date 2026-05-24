package com.techub.api.repository;

import com.techub.api.domain.Bio;

import java.util.List;
import java.util.Optional;

public interface BioRepository extends SoftDeleteRepository<Bio, Long> {

    List<Bio> findAllByOrderByIdAsc();

    Optional<Bio> findByDescription(String description);
}
