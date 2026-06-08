package com.techub.api.controller;

import com.techub.api.domain.Student;
import com.techub.api.service.CurrentUserService;
import com.techub.api.service.LikesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/resumos")
public class LikesController {

    @Autowired
    private LikesService likesService;

    @Autowired
    private CurrentUserService currentUserService;

    @PostMapping("/{id}/like")
    public ResponseEntity<?> curtir(
            @PathVariable Long id) {

        Student student = currentUserService.getCurrentStudent();

        try {
            String resultado = likesService.curtir(id, student.getId());
            return ResponseEntity.ok(resultado);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{id}/likes")
    public ResponseEntity<?> contarCurtidas(@PathVariable Long id) {
        try {
            long total = likesService.contarCurtidas(id);
            return ResponseEntity.ok("Total de curtidas: " + total);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
