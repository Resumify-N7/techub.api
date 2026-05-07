package com.techub.api.repository;

import com.techub.api.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);  //se o email ja estiver cadastrado ele nao deixa finalizar o cadastr
    Optional<User> findByStudent_Id(Long studentId);
    boolean existsByEmailIgnoreCase(String email);
}