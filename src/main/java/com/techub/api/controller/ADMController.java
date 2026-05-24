package com.techub.api.controller;

import com.techub.api.domain.ADM;
import com.techub.api.dto.ADMCreateRequestDTO;
import com.techub.api.dto.ADMGetResponseDTO;
import com.techub.api.service.ADMService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/adm")
public class ADMController {

    @Autowired
    private ADMService admService;

    @GetMapping
    public List<ADM> listar(@RequestParam(defaultValue = "20") int limit){
        return admService.listar_adm(limit);
    }
}
