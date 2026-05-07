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
    public List<ADM> listar(){
        return admService.listar_adm();
    }

    @PatchMapping("/desativar")
    public ResponseEntity<?> desativar_adm(@PathVariable Long id){
        admService.desativar_adm(id);
        return ResponseEntity.ok("Sucesso ao desativar ADM");
    }

    @PatchMapping("/ativar")
    public ResponseEntity<?> ativar_adm(@PathVariable Long id){
        admService.ativar(id);
        return ResponseEntity.ok("Sucesso ao ativar ADM");
    }
}
