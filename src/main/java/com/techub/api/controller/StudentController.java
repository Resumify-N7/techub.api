package com.techub.api.controller;

import com.techub.api.domain.Student;
import com.techub.api.dto.CourseChangeDTO;
import com.techub.api.dto.StudentGetDTO;
import com.techub.api.dto.UserLoginDataDTO;
import com.techub.api.dto.UserUpdateStudentRequestDTO;
import com.techub.api.repository.StudentRepository;
import com.techub.api.service.CurrentUserService;
import com.techub.api.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/student")
public class StudentController {

    final private StudentService studentService;
    final private StudentRepository studentRepository;
    final private CurrentUserService currentUserService;

    public StudentController(StudentService studentService, StudentRepository studentRepository, CurrentUserService currentUserService){
        this.studentService = studentService;
        this.studentRepository = studentRepository;
        this.currentUserService = currentUserService;
    }

    @GetMapping("/pontuacao/{id}")
    public Integer obterPontuacao(@PathVariable Long id){ return studentService.obter_pontuacao(id); }

    @PutMapping("/{id}")
    public String atualizarPerfil(@PathVariable Long id, @RequestBody UserUpdateStudentRequestDTO dto){
        studentService.atualizar_perfil(id, dto);
        return "Dados atualizado com sucesso!";
    }

    @GetMapping
    public List<Student> listarAlunos(@RequestParam(defaultValue = "20") int limit){
        return studentService.listar(limit);
    }

    @GetMapping("/{id}")
    public StudentGetDTO busarPerfilId(@PathVariable Long id) {
        return studentService.buscar_perfilId(id);
    }

    @GetMapping("/admin/{id}")
    @PreAuthorize("hasRole('ADM')")
    public StudentGetDTO busarPerfilIdAsAdmin(@PathVariable Long id) {
        return studentService.buscar_perfilIdAsAdmin(id);
    }

    @GetMapping("/me")
    public StudentGetDTO busarPerfilToken() {
        Student me = currentUserService.getCurrentStudent();
        return new StudentGetDTO(
                me.getAtivo(),
                me.getId(),
                me.getNome(),
                me.getSemestre(),
                me.getBio(),
                me.getAvatar(),
                me.getCourse(),
                me.getPontuacao(),
                me.getSeguindoCurrentUser(),
                me.getSeguidoPeloCurrentUser(),
                studentRepository.countFollowers(me.getId()),
                studentRepository.countFollowing(me.getId())
        );
    }

    @PatchMapping("/{id}")
    public String atualizar_status_student(@PathVariable Long id){
        studentService.atualizar_status(id);
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

