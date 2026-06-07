package com.techub.api.repository;

import com.techub.api.domain.ADM;

public interface ADMRepository extends SoftDeleteRepository<ADM, Long> {
    boolean existsByUsernameIgnoreCase(String username);
    long count();
}
