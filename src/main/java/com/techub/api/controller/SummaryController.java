package com.techub.api.controller;

import com.techub.api.domain.Student;
import com.techub.api.dto.SummaryCreateRequestDTO;
import com.techub.api.dto.SummaryGetResponseDTO;
import com.techub.api.dto.SummaryListResponseDTO;
import com.techub.api.dto.SummaryUpdateRequestDTO;
import com.techub.api.service.CurrentUserService;
import com.techub.api.service.StudentService;
import com.techub.api.service.SummaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/resumos")
public class SummaryController {

    @Autowired
    private SummaryService service;

    @Autowired
    private CurrentUserService currentUserService;

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
    public List<SummaryListResponseDTO> listar(@RequestParam(defaultValue = "20") int limit) {
        return service.getAll(limit);
    }

    @GetMapping("/ativos")
    public List<SummaryGetResponseDTO> listarResumosAtivados(@RequestParam(defaultValue = "20") int limit) {
        return service.findByAtivoTrue(limit);
    }

    @GetMapping("/me")
    public List<SummaryListResponseDTO> getStudentSummary(@RequestParam(defaultValue = "20") int limit) {
        Student student = currentUserService.getCurrentStudent();

        return service.getStudentSummary(student.getId(), limit);
    }

    @GetMapping("/student/{studentId}")
    public List<SummaryListResponseDTO> getStudentSummaryByStudentId(
            @PathVariable Long studentId,
            @RequestParam(defaultValue = "20") int limit
    ) {
        return service.getStudentSummaryByStudentId(studentId, limit);
    }

    @GetMapping("/subject/{subjectId}")
    public List<SummaryListResponseDTO> getBySubject(@PathVariable Long subjectId,
                                                    @RequestParam(defaultValue = "20") int limit) {
        return service.getBySubjectId(subjectId, limit);
    }

    @GetMapping("/desativados")
    public List<SummaryGetResponseDTO> listarResumosDesativados(@RequestParam(defaultValue = "20") int limit) {
        return service.findByAtivoFalse(limit);
    }

    @GetMapping("/{id}")
    public SummaryGetResponseDTO buscar(@PathVariable Long id) {
        return service.getById(id);
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
        var student = currentUserService.getCurrentStudent();

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
        return ResponseEntity.ok("Sucesso ao criar ao apagar Resumo");
    }

    @PatchMapping("/{summaryId}/visibilidade")
    public ResponseEntity<?> alternarVisibilidade(@PathVariable Long summaryId) {
        var student = currentUserService.getCurrentStudent();

        try {
            service.alternarVisibilidade(summaryId, student.getId());
            return ResponseEntity.ok("Sucesso ao alterar visibilidade. Resumo:" + summaryId + "Student: " + student.getId());
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}