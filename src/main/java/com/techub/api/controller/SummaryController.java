package com.techub.api.controller;

import com.techub.api.domain.Student;
import com.techub.api.dto.FeedDTO;
import com.techub.api.dto.SummaryCreateRequestDTO;
import com.techub.api.dto.SummaryGetResponseDTO;
import com.techub.api.dto.SummaryUpdateRequestDTO;
import com.techub.api.service.CurrentUserService;
import com.techub.api.service.SummaryService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/resumos")
public class SummaryController {

    private final SummaryService service;
    private final CurrentUserService currentUserService;

    public SummaryController(SummaryService service, CurrentUserService currentUserService) {
        this.service = service;
        this.currentUserService = currentUserService;
    }

    @PostMapping
    public ResponseEntity<?> criar(@RequestBody SummaryCreateRequestDTO dto) {
        Student student = currentUserService.getCurrentStudent();
        try {
            return ResponseEntity.ok(service.saveSummary(dto, student.getId()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping
    public List<SummaryGetResponseDTO> listar(@RequestParam(defaultValue = "20") int limit) {
        return service.getAll(limit);
    }

    @GetMapping("/ativos")
    public ResponseEntity<FeedDTO> listarAtivos(
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        return ResponseEntity.ok(service.findByAtivoTruePaged(page, size));
    }

    @GetMapping("/ranking")
    public ResponseEntity<FeedDTO> ranking(
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        return ResponseEntity.ok(service.getRankingPaged(page, size));
    }

    @GetMapping("/me")
    public List<SummaryGetResponseDTO> getStudentSummary(@RequestParam(defaultValue = "20") int limit) {
        Student student = currentUserService.getCurrentStudent();
        return service.getStudentSummary(student.getId(), limit);
    }

    @GetMapping("/student/{studentId}")
    public List<SummaryGetResponseDTO> getStudentSummaryByStudentId(
            @PathVariable Long studentId,
            @RequestParam(defaultValue = "20") int limit
    ) {
        return service.getStudentSummaryByStudentId(studentId, limit);
    }

    @GetMapping("/subject/{subjectId}")
    public List<SummaryGetResponseDTO> getBySubject(
            @PathVariable Long subjectId,
            @RequestParam(defaultValue = "20") int limit
    ) {
        return service.getBySubjectId(subjectId, limit);
    }

    @GetMapping("/desativados")
    public List<SummaryGetResponseDTO> listarDesativados(@RequestParam(defaultValue = "20") int limit) {
        return service.findByAtivoFalse(limit);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SummaryGetResponseDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @GetMapping("/admin/{id}")
    @PreAuthorize("hasRole('ADM')")
    public ResponseEntity<SummaryGetResponseDTO> getByIdAsAdmin(@PathVariable Long id) {
        return ResponseEntity.ok(service.getByIdAsAdmin(id));
    }

    @PutMapping("/{id}")
    public SummaryGetResponseDTO atualizar(@PathVariable Long id, @RequestBody SummaryUpdateRequestDTO dto) {
        return service.update(id, dto);
    }

    @PatchMapping("/atualizar_status/{id}")
    public ResponseEntity<?> atualizar_status(@PathVariable Long id) {
        service.atualizar_status(id);
        return ResponseEntity.ok("Sucesso ao atualizar o status do Resumo");
    }

    @PatchMapping("/reportar/{id}")
    public ResponseEntity<?> reportar(@PathVariable Long id) {
        Student student = currentUserService.getCurrentStudent();
        try {
            service.reportar(id, student.getId());
            return ResponseEntity.ok("Sucesso ao reportar o Resumo");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletar(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.ok("Sucesso ao apagar Resumo");
    }

    @PatchMapping("/{summaryId}/visibilidade")
    public ResponseEntity<?> alternarVisibilidade(@PathVariable Long summaryId) {
        Student student = currentUserService.getCurrentStudent();
        try {
            service.alternarVisibilidade(summaryId, student.getId());
            return ResponseEntity.ok("Sucesso ao alterar visibilidade. Resumo: " + summaryId);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}