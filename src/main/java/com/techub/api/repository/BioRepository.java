package com.techub.api.repository;

import com.techub.api.domain.Bio;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BioRepository extends JpaRepository<Bio, Long> {

    List<Bio> findAllByOrderByIdAsc();

    Optional<Bio> findByDescription(String description);
}
