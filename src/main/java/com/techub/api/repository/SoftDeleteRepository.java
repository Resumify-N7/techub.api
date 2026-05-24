package com.techub.api.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface SoftDeleteRepository<T, ID> extends JpaRepository<T, ID> {

    @Query("select e from #{#entityName} e where coalesce(e.ativo, true) = true")
    Page<T> findActive(Pageable pageable);

    @Query("select e from #{#entityName} e where e.ativo = false")
    Page<T> findInactive(Pageable pageable);
}