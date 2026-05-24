package com.techub.api.controller;

import com.techub.api.domain.Avatar;
import com.techub.api.service.AvatarService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/avatar")
public class AvatarController {

    private final AvatarService avatarService;

    public AvatarController(AvatarService avatarService) {
        this.avatarService = avatarService;
    }

    @GetMapping
    public List<Avatar> listar(@RequestParam(required = false) Boolean male,
                               @RequestParam(defaultValue = "20") int limit) {
        return avatarService.listar(male, limit);
    }
}