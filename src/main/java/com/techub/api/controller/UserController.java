package com.techub.api.controller;

import com.techub.api.domain.User;

import com.techub.api.dto.*;
import com.techub.api.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping
    public ResponseEntity<?> criar_usuario_aluno(@RequestBody UserCreateStudentRequestDTO dto) {
        User user = userService.cadastrarAluno(dto);
        return ResponseEntity.ok( new UserCreateResponseDTO("Usuario criado com sucesso", user.getId()));
    }

    @PostMapping("/adm")
    public ResponseEntity<?> criar_usuario_adm(ADMCreateRequestDTO dto){
        userService.cadastrarADM(dto);
        return ResponseEntity.ok("Sucesso ao criar ADM");
    }

    @GetMapping
    public List<UserGetResponseDTO> listarUser() {
        return userService.listar();
    }

    //localhost:8080/api/usuarios/3
    @GetMapping("/{id}")
    public User buscarUserPorId(@PathVariable Long id) {
        return userService.buscar_por_id(id).orElse(null);
    }

    @PutMapping("/{id}")
    public String atualizarDadosLogin(@PathVariable Long id, @RequestBody UserLoginDataDTO dto){
        userService.atualizar_dados_login(id, dto);
        return "Dados atualizado com sucesso!";
    }

    // Ao apagar apaga tudo associado a essa tabela
    @DeleteMapping("/{id}")
    public String deletarUserPorId(@PathVariable Long id) {
        userService.deletar(id);
        return "Dados apagados com sucesso!";
    }

    @GetMapping("/role/{id}")
    public UserRoleResponse descobrirRole(@PathVariable Long id) {
        return userService.descobrirRole(id);
    }

}