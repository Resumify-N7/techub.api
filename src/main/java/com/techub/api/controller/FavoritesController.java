package com.techub.api.controller;

import com.techub.api.domain.Student;
import com.techub.api.dto.SummaryListResponseDTO;
import com.techub.api.service.CurrentUserService;
import com.techub.api.service.FavoritesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/resumos/favoritos")
public class FavoritesController {

    @Autowired
    private FavoritesService favoritesService;

    @Autowired
    private CurrentUserService currentUserService;

    @GetMapping("/me")
    public ResponseEntity<List<SummaryListResponseDTO>> meusFavoritos(@RequestParam(defaultValue = "20") int limit) {
        Student me = currentUserService.getCurrentStudent();
        return ResponseEntity.ok(favoritesService.getMyFavorites(me.getId(), limit));
    }
}