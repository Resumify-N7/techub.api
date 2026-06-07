package com.techub.api.repository;

import com.techub.api.domain.Avatar;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface AvatarRepository extends SoftDeleteRepository<Avatar, Long> {
    List<Avatar> findAllByOrderByIdAsc();
    List<Avatar> findByMaleTrueOrderByIdAsc();
    List<Avatar> findByMaleFalseOrderByIdAsc();
    Page<Avatar> findByMaleTrueAndAtivoTrue(Pageable pageable);
    Page<Avatar> findByMaleFalseAndAtivoTrue(Pageable pageable);
    Optional<Avatar> findByUrl(String url);
    long countByAtivoTrue();
    long countByAtivoFalse();
}