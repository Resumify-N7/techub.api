package com.techub.api.service;

import com.techub.api.domain.Student;
import com.techub.api.domain.User;
import com.techub.api.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class CurrentUserService {

    private final UserRepository userRepository;

    public CurrentUserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuário não autenticado");
        }

        Object principal = authentication.getPrincipal();
        String email = null;

        if (principal instanceof User user) {
            return user;
        }

        if (principal instanceof UserDetails userDetails) {
            email = userDetails.getUsername();
        } else if (principal instanceof String principalName) {
            email = principalName;
        }

        if (email == null || email.isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuário autenticado inválido");
        }

        return userRepository.findByEmailAndAtivoTrue(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuário autenticado não encontrado"));
    }

    public Student getCurrentStudent() {
        User currentUser = getCurrentUser();
        Student student = currentUser.getStudent();

        if (student == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Perfil de estudante não vinculado ao usuário");
        }

        return student;
    }

    public Student getCurrentStudentOrNull() {
        User currentUser = getCurrentUser();
        return currentUser.getStudent();
    }

    public String getCurrentEmail() {
        return getCurrentUser().getEmail();
    }
}