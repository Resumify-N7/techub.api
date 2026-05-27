package com.techub.api.controller;

import com.techub.api.domain.Course;
import com.techub.api.domain.Student;
import com.techub.api.domain.Subject;
import com.techub.api.dto.StudentGetSimpleResponseDTO;
import com.techub.api.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/courses")
public class CourseController {

    @Autowired
    private CourseService courseService;

    @PostMapping
    public ResponseEntity<Course> criar(@RequestBody Course course) {
        return ResponseEntity.ok(courseService.criar(course));
    }

    @GetMapping
    public ResponseEntity<List<Course>> listar(@RequestParam(defaultValue = "20") int limit) {
        return ResponseEntity.ok(courseService.listar(limit));
    }

    @GetMapping("/{id}/students")
    public ResponseEntity<List<Student>> listarStudents(
            @PathVariable Long id,
            @RequestParam(defaultValue = "20") int limit) {
        return ResponseEntity.ok(courseService.listarStudentsPorCurso(id, limit));
    }

    @GetMapping("/{id}/subjects")
    public ResponseEntity<List<Subject>> listarSubjects(@PathVariable Long id) {
        return ResponseEntity.ok(courseService.listarSubjectsPorCurso(id));
    }

    @GetMapping("/{id}/semestres/{semestre}/students")
    public ResponseEntity<List<StudentGetSimpleResponseDTO>> listarStudentsPorSemestre(
            @PathVariable Long id,
            @PathVariable Integer semestre,
            @RequestParam(defaultValue = "20") int limit) {
        return ResponseEntity.ok(courseService.listarStudentsPorCursoESemestre(id, semestre, limit));
    }

    @GetMapping("semestres/students/me")
    public ResponseEntity<List<Subject>> listarMinhasMateriasPorSemestre(
            @RequestParam(defaultValue = "20") int limit) {
        return ResponseEntity.ok(courseService.listarMinhasMaterias(limit));
    }

    @GetMapping("/{id}/semestres/{semestre}/subjects")
    public ResponseEntity<List<Subject>> listarSubjectsPorSemestre(
            @PathVariable Long id,
            @PathVariable Integer semestre,
            @RequestParam(defaultValue = "20") int limit) {
        return ResponseEntity.ok(courseService.listarSubjectsPorCursoESemestre(id, semestre, limit));
    }

    @PatchMapping("/atualizar_status/{id}")
    public ResponseEntity<?> atualizar_status(@PathVariable Long id) {
        courseService.atualizar_status(id);
        return ResponseEntity.ok("Sucesso ao atualizar o status do Curso");
    }

    @GetMapping("/desativados")
    public List<Course> listar_desativados(@RequestParam(defaultValue = "20") int limit) {
       return courseService.listarCursosDesativados(limit);
    }
}