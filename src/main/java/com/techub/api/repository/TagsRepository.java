package com.techub.api.repository;

import com.techub.api.domain.Tags;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TagsRepository extends JpaRepository<Tags, Long> {
    boolean existsByNameIgnoreCase(String name);
}
