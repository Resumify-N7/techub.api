package com.techub.api.controller;

import com.techub.api.domain.Professor;
import com.techub.api.domain.User;
import com.techub.api.dto.ProfessorGetDTO;
import com.techub.api.repository.UserRepository;
import com.techub.api.service.CurrentUserService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/professor")
public class ProfessorController {

    private final CurrentUserService currentUserService;
    private final UserRepository userRepository;

    public ProfessorController(CurrentUserService currentUserService, UserRepository userRepository) {
        this.currentUserService = currentUserService;
        this.userRepository = userRepository;
    }

    // Perfil do professor autenticado
    @GetMapping("/me")
    public ProfessorGetDTO getMe() {
        User user = currentUserService.getCurrentUser();
        Professor professor = user.getProfessor();

        if (professor == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Perfil de professor não vinculado ao usuário");
        }

        return toDTO(professor, user.getEmail());
    }

    // Perfil público de qualquer professor pelo ID do Professor
    @GetMapping("/{id}")
    public ProfessorGetDTO getById(@PathVariable Long id) {
        User user = userRepository.findByProfessorId(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Professor não encontrado"));

        return toDTO(user.getProfessor(), user.getEmail());
    }

    private ProfessorGetDTO toDTO(Professor professor, String email) {
        return new ProfessorGetDTO(
                professor.getId(),
                professor.getNome(),
                professor.getBio(),
                professor.getAvatar(),
                professor.getSubject(),
                email
        );
    }
}