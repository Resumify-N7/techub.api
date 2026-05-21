package com.techub.api.controller;

import com.techub.api.domain.Student;
import com.techub.api.dto.SummaryCreateRequestDTO;
import com.techub.api.dto.SummaryGetResponseDTO;
import com.techub.api.dto.SummaryUpdateRequestDTO;
import com.techub.api.service.JwtService;
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
    private JwtService jwtService;

    @Autowired
    private StudentService studentService;

    @PostMapping
    public ResponseEntity<?> criar(@CookieValue(name = "accessToken", required = false) String token , @RequestBody SummaryCreateRequestDTO dto) {
        if(token == null || token.isBlank()) {
            throw  new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Token ausente"
            );
        }

        String email = jwtService.extractEmail(token);

        Student student = studentService.buscar_perfilEmail(email);

        try {
            return ResponseEntity.ok(service.saveSummary(dto, student.getId()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping
    public List<SummaryGetResponseDTO> listar() {
        return service.getAll();
    }

    @GetMapping("/ativos")
    public List<SummaryGetResponseDTO> listarResumosAtivados() {
        return service.findByAtivoTrue()    ;
    }

    @GetMapping("/desativados")
    public List<SummaryGetResponseDTO> listarResumosDesativados() {
        return service.findByAtivoFalse();
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
    public ResponseEntity<?> reportar(
            @CookieValue(name = "accessToken", required = false) String token,
            @PathVariable Long id) {

        if (token == null || token.isBlank()) {
            return ResponseEntity.status(401).body("Token ausente");
        }

        String email = jwtService.extractEmail(token);
        var student = studentService.buscar_perfilEmail(email);

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
    public ResponseEntity<?> alternarVisibilidade(
            @CookieValue(name = "accessToken", required = false) String token,
            @PathVariable Long summaryId) {

        if (token == null || token.isBlank()) {
            return ResponseEntity.status(401).body("Token ausente");
        }

        String email = jwtService.extractEmail(token);
        var student = studentService.buscar_perfilEmail(email);

        try {
            service.alternarVisibilidade(summaryId, student.getId());
            return ResponseEntity.ok("Sucesso ao alterar visibilidade. Resumo:" + summaryId + "Student: " + student.getId());
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}