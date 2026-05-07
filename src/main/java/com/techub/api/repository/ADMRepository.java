package com.techub.api.repository;

import com.techub.api.domain.ADM;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ADMRepository extends JpaRepository<ADM, Long> {
    boolean existsByUsernameIgnoreCase(String username);
}
