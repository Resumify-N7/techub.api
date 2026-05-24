package com.techub.api.repository;

import com.techub.api.domain.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends SoftDeleteRepository<User, Long> {
    List<User> findByAtivoTrue();
    List<User> findByAtivoFalse();
    Optional<User> findByEmail(String email);
    Optional<User> findByEmailAndAtivoTrue(String email);
    boolean existsByEmail(String email);  //se o email ja estiver cadastrado ele nao deixa finalizar o cadastr
    Optional<User> findByStudent_Id(Long studentId);
    boolean existsByEmailIgnoreCase(String email);
}