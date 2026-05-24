package com.techub.api.repository;

import com.techub.api.domain.Tags;

public interface TagsRepository extends SoftDeleteRepository<Tags, Long> {
    boolean existsByNameIgnoreCase(String name);
}
