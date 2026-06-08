package com.techub.api.repository;

import com.techub.api.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends SoftDeleteRepository<User, Long> {
    List<User> findByAtivoTrue();
    List<User> findByAtivoFalse();
    Optional<User> findByEmail(String email);
    Optional<User> findByEmailAndAtivoTrue(String email);
    boolean existsByEmail(String email);
    Optional<User> findByStudent_Id(Long studentId);
    boolean existsByEmailIgnoreCase(String email);
    long countByAtivoTrue();
    long countByAtivoFalse();
    Page<User> findByAtivoTrue(Pageable pageable);
    Optional<User> findByProfessorId(Long professorId);
}
