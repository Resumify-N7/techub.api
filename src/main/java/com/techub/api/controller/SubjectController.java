package com.techub.api.controller;

import com.techub.api.domain.Subject;
import com.techub.api.dto.SubjectListResponseDTO;
import com.techub.api.service.SubjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/subjects")
public class SubjectController {

    @Autowired
    private SubjectService subjectService;

    @PostMapping
    public ResponseEntity<Subject> criar(@RequestBody Subject subject) {
        return ResponseEntity.ok(subjectService.criar(subject));
    }

    @GetMapping
    public ResponseEntity<List<SubjectListResponseDTO>> listar(@RequestParam(defaultValue = "20") int limit) {
        return ResponseEntity.ok(subjectService.listar(limit));
    }

    @GetMapping("/ativos")
    public ResponseEntity<List<SubjectListResponseDTO>> listarAtivos(@RequestParam(defaultValue = "40") int limit) {
        return ResponseEntity.ok(subjectService.listar(limit));
    }

    @GetMapping("/desativados")
    public ResponseEntity<List<SubjectListResponseDTO>> listarDesativados(@RequestParam(defaultValue = "20") int limit) {
        return ResponseEntity.ok(subjectService.listarDesativados(limit));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Subject> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(subjectService.buscarPorId(id));
    }

    @GetMapping("/course/{courseId}")
    public ResponseEntity<List<SubjectListResponseDTO>> listarPorCurso(
            @PathVariable Long courseId,
            @RequestParam(defaultValue = "20") int limit) {
        return ResponseEntity.ok(subjectService.listarPorCurso(courseId, limit));
    }

    @GetMapping("/course/{courseId}/semestre/{semestre}")
    public ResponseEntity<List<SubjectListResponseDTO>> listarPorCursoESemestre(
            @PathVariable Long courseId,
            @PathVariable Integer semestre,
            @RequestParam(defaultValue = "20") int limit) {
        return ResponseEntity.ok(subjectService.listarPorCursoESemestre(courseId, semestre, limit));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Subject> atualizar(@PathVariable Long id, @RequestBody Subject subject) {
        return ResponseEntity.ok(subjectService.atualizar(id, subject));
    }

    @PatchMapping("/atualizar_status/{id}")
    public ResponseEntity<?> atualizar_status(@PathVariable Long id) {
        subjectService.atualizar_status(id);
        return ResponseEntity.ok("Sucesso ao atualizar o status da Matéria");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletar(@PathVariable Long id) {
        subjectService.deletar(id);
        return ResponseEntity.ok("Sucesso ao apagar Matéria");
    }
}