package com.techub.api.service;

import com.techub.api.domain.Course;
import com.techub.api.domain.Student;
import com.techub.api.domain.Subject;
import com.techub.api.dto.StudentGetSimpleResponseDTO;
import com.techub.api.repository.CourseRepository;
import com.techub.api.repository.StudentRepository;
import com.techub.api.repository.SubjectRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CourseService {

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private SubjectRepository subjectRepository;

    @Autowired
    private CurrentUserService currentUserService;

    public Course criar(Course course) {
        if(!courseRepository.existsByNameIgnoreCase(course.getName())){
            return courseRepository.save(course);
        }

        throw new RuntimeException(("Nome de curso já usado"));
    }

    public List<Course> listar(int limit) {
        int pageSize = Math.max(1, limit);
        return courseRepository.findActive(PageRequest.of(0, pageSize)).getContent();
    }

    public List<Student> listarStudentsPorCurso(Long cursoId, int limit) {
        Course course = courseRepository.findById(cursoId)
                .orElseThrow(() -> new RuntimeException("Curso não encontrado"));
        
        int pageSize = Math.max(1, limit);
        return studentRepository.findByCourse(course, PageRequest.of(0, pageSize)).getContent();
    }

    public List<Subject> listarMinhasMaterias(int limit){
        Student student = currentUserService.getCurrentStudent();

        return listarSubjectsPorCursoESemestre(student.getCourse().getId(), student.getSemestre(), limit);
    }

    public List<Subject> listarSubjectsPorCurso(Long cursoId) {
        Course course = courseRepository.findById(cursoId)
                .orElseThrow(() -> new RuntimeException("Curso não encontrado"));
        
        return subjectRepository.findByCourse(course);
    }

    public List<StudentGetSimpleResponseDTO> listarStudentsPorCursoESemestre(Long cursoId, Integer semestre, int limit) {
        Course course = courseRepository.findById(cursoId)
                .orElseThrow(() -> new RuntimeException("Curso não encontrado"));

        int pageSize = Math.max(1, limit);
        List<Student> students = studentRepository.findByCourseAndSemestre(course, semestre, PageRequest.of(0, pageSize)).getContent();
        return students.stream().map(student -> new StudentGetSimpleResponseDTO(
                student.getId(),
                student.getNome(),
                student.getSemestre(),
                student.getAvatar().getUrl(),
                student.getFollowers().size()
        )).toList();
    }

    public List<Subject> listarSubjectsPorCursoESemestre(Long cursoId, Integer semestre, int limit) {
        Course course = courseRepository.findById(cursoId)
                .orElseThrow(() -> new RuntimeException("Curso não encontrado"));

        int pageSize = Math.max(1, limit);
        return subjectRepository.findByCourseAndSemestre(course, semestre, PageRequest.of(0, pageSize)).getContent();
    }
}