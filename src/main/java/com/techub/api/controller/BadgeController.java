package com.techub.api.controller;

import com.techub.api.domain.Badge;
import com.techub.api.service.BadgeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/badges")
public class BadgeController {

    private final BadgeService badgeService;

    public BadgeController(BadgeService badgeService) {
        this.badgeService = badgeService;
    }

    @PostMapping
    public ResponseEntity<Badge> criar(@RequestBody Badge badge) {
        return ResponseEntity.ok(badgeService.criar(badge));
    }

    @GetMapping
    public ResponseEntity<List<Badge>> listar(@RequestParam(defaultValue = "20") int limit) {
        return ResponseEntity.ok(badgeService.listar(limit));
    }

    @GetMapping("/ativos")
    public ResponseEntity<List<Badge>> listarAtivos(@RequestParam(defaultValue = "20") int limit) {
        return ResponseEntity.ok(badgeService.listarAtivos(limit));
    }

    @GetMapping("/desativados")
    public ResponseEntity<List<Badge>> listarDesativados(@RequestParam(defaultValue = "20") int limit) {
        return ResponseEntity.ok(badgeService.listarDesativados(limit));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Badge> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(badgeService.buscarPorId(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Badge> atualizar(@PathVariable Long id, @RequestBody Badge badge) {
        return ResponseEntity.ok(badgeService.atualizar(id, badge));
    }

    @PatchMapping("/atualizar_status/{id}")
    public ResponseEntity<?> atualizar_status(@PathVariable Long id) {
        badgeService.atualizar_status(id);
        return ResponseEntity.ok("Sucesso ao atualizar o status do Badge");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletar(@PathVariable Long id) {
        badgeService.deletar(id);
        return ResponseEntity.ok("Sucesso ao apagar Badge");
    }
}