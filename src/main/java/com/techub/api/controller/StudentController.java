package com.techub.api.controller;

import com.techub.api.domain.Student;
import com.techub.api.dto.CourseChangeDTO;
import com.techub.api.dto.UserLoginDataDTO;
import com.techub.api.dto.UserUpdateStudentRequestDTO;
import com.techub.api.repository.CourseChangeRepository;
import com.techub.api.service.JwtService;
import com.techub.api.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/student")
public class StudentController {

    @Autowired
    private StudentService studentService;

    @Autowired
    private JwtService jwtService;

    @GetMapping("/pontuacao/{id}")
    public Integer obterPontuacao(@PathVariable Long id){ return studentService.obter_pontuacao(id); }

    @PutMapping("/{id}")
    public String atualizarPerfil(@PathVariable Long id, @RequestBody UserUpdateStudentRequestDTO dto){
        studentService.atualizar_perfil(id, dto);
        return "Dados atualizado com sucesso!";
    }

    @GetMapping
    public List<Student> listarAlunos(){
        return studentService.listar();
    }

    @GetMapping("/{id}")
    public Student busarPerfilId(@PathVariable Long id) { return studentService.buscar_perfilId(id); }

    @GetMapping("/me")
    public Student busarPerfilToken(@CookieValue(name = "accessToken", required = false) String token) {
        if(token == null || token.isBlank()) {
            throw  new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Token ausente"
            );
        }

        String email = jwtService.extractEmail(token);

        return studentService.buscar_perfilEmail(email);
    }

    @PatchMapping("/{id}")
    public String atualizar_status_student(@PathVariable Long id){
        studentService.deletar(id);
        return "Status do Aluno alterado com sucesso!";
    }

    // ainda não está apagando por conta do cascade
    @DeleteMapping("/{id}")
    public String deleter_student(@PathVariable Long id){
        studentService.deletar(id);
        return "Dados apagados com sucesso!";
    }

    @GetMapping("/loginData/{id}")
    public UserLoginDataDTO obterDadosLogin(@PathVariable Long id){
        return studentService.obter_dados_login(id);
    }



    // aqui mexe aqui tbmm
    @PutMapping("/{id}/trocar-curso")
    public ResponseEntity<?> trocarCurso(
            @PathVariable Long id,
            @RequestBody CourseChangeDTO dto
    ) {
        studentService.trocarCurso(id, dto.getCourseId());
        return ResponseEntity.ok("Curso alterado com sucesso");
    }
    }

