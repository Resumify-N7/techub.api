package com.techub.api.repository;

import com.techub.api.domain.Avatar;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AvatarRepository extends JpaRepository<Avatar, Long> {

    List<Avatar> findAllByOrderByIdAsc();

    List<Avatar> findByMaleTrueOrderByIdAsc();

    List<Avatar> findByMaleFalseOrderByIdAsc();

    Optional<Avatar> findByUrl(String url);
}