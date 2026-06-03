package com.techub.api.controller;

import com.techub.api.domain.Tags;
import com.techub.api.service.TagsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tags")
public class TagsController {

    private final TagsService tagsService;

    public TagsController(TagsService tagsService) {
        this.tagsService = tagsService;
    }

    @PostMapping
    public ResponseEntity<Tags> criar(@RequestBody Tags tag) {
        return ResponseEntity.ok(tagsService.criar(tag));
    }

    @GetMapping
    public ResponseEntity<List<Tags>> listar(@RequestParam(defaultValue = "20") int limit) {
        return ResponseEntity.ok(tagsService.listar(limit));
    }

    @GetMapping("/ativos")
    public ResponseEntity<List<Tags>> listarAtivos(@RequestParam(defaultValue = "20") int limit) {
        return ResponseEntity.ok(tagsService.listarAtivos(limit));
    }

    @GetMapping("/desativados")
    public ResponseEntity<List<Tags>> listarDesativados(@RequestParam(defaultValue = "20") int limit) {
        return ResponseEntity.ok(tagsService.listarDesativados(limit));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Tags> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(tagsService.buscarPorId(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Tags> atualizar(@PathVariable Long id, @RequestBody Tags tag) {
        return ResponseEntity.ok(tagsService.atualizar(id, tag));
    }

    @PatchMapping("/atualizar_status/{id}")
    public ResponseEntity<?> atualizar_status(@PathVariable Long id) {
        tagsService.atualizar_status(id);
        return ResponseEntity.ok("Sucesso ao atualizar o status da Tag");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletar(@PathVariable Long id) {
        tagsService.deletar(id);
        return ResponseEntity.ok("Sucesso ao apagar Tag");
    }
}