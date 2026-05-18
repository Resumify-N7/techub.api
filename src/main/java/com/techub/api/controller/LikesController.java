package com.techub.api.controller;

import com.techub.api.domain.Student;
import com.techub.api.service.JwtService;
import com.techub.api.service.LikesService;
import com.techub.api.service.StudentService;
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
    private JwtService jwtService;  // extrai o email do token JWT

    @Autowired
    private StudentService studentService;  // busca o aluno pelo email

    // curtir ou descurtir um resumo
    @PostMapping("/{id}/like")
    public ResponseEntity<?> curtir(
            @CookieValue(name = "accessToken", required = false) String token,
            @PathVariable Long id) {

        // Verifica se o usuário está logado
        if (token == null || token.isBlank()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token ausente");
        }

        // Extrai o email do token e busca o aluno logado
        String email = jwtService.extractEmail(token);
        Student student = studentService.buscar_perfilEmail(email);

        try {
            String resultado = likesService.curtir(id, student.getId());
            return ResponseEntity.ok(resultado);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // retorna total de curtidas de um resumo
    @GetMapping("/{id}/likes")
    public ResponseEntity<?> contarCurtidas(@PathVariable Long id) {
        try {
            long total = likesService.contarCurtidas(id);
            return ResponseEntity.ok("Total de curtidas: " + total);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    // GET /ranking — retorna resumos ordenados pelos mais curtidos
    @GetMapping("/ranking")
    public ResponseEntity<?> getRanking() {
        try {
            return ResponseEntity.ok(likesService.getRanking());
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
