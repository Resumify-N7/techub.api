package com.techub.api.controller;

import com.techub.api.domain.Summary;
import com.techub.api.service.JwtService;
import com.techub.api.service.StudentService;
import com.techub.api.service.SummaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/resumos")
public class SummaryController {

    @Autowired
    private SummaryService service;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private StudentService studentService;

    @PostMapping
    public ResponseEntity<?> criar(@RequestBody Summary summary) {
        try {
            return ResponseEntity.ok(service.saveSummary(summary));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping
    public List<Summary> listar() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public Summary buscar(@PathVariable Long id) {
        return service.getById(id);
    }

    @PutMapping("/{id}")
    public Summary atualizar(@PathVariable Long id, @RequestBody Summary summary) {
        return service.update(id, summary);
    }

    @DeleteMapping("/{id}")
    public void deletar(@PathVariable Long id) {
        service.delete(id);
    }


    @PatchMapping("/{id}/visibilidade")
    public ResponseEntity<?> alternarVisibilidade(
            @CookieValue(name = "accessToken", required = false) String token,
            @PathVariable Long id) {

        if (token == null || token.isBlank()) {
            return ResponseEntity.status(401).body("Token ausente");
        }

        String email = jwtService.extractEmail(token);
        var student = studentService.buscar_perfilEmail(email);

        try {
            return ResponseEntity.ok(service.alternarVisibilidade(id, student.getId()));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}