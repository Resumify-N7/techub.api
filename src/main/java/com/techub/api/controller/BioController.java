package com.techub.api.controller;

import com.techub.api.domain.Bio;
import com.techub.api.service.BioService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/bio")
public class BioController {

    private final BioService bioService;

    public BioController(BioService bioService) {
        this.bioService = bioService;
    }

    @GetMapping
    public List<Bio> listar() {
        return bioService.listar();
    }
}
