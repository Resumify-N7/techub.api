package com.techub.api.service;

import com.techub.api.domain.Course;
import com.techub.api.domain.CourseChange;
import com.techub.api.domain.Student;
import com.techub.api.domain.User;
import com.techub.api.dto.UserLoginDataDTO;
import com.techub.api.dto.UserUpdateStudentRequestDTO;
import com.techub.api.repository.CourseChangeRepository;
import com.techub.api.repository.CourseRepository;
import com.techub.api.repository.StudentRepository;
import com.techub.api.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class StudentService {
    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private CourseChangeRepository courseChangeRepository;

    public Student buscar_por_id(Long id) {
        return studentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario não encontrado"));
    }

    public Integer obter_pontuacao(Long id){ return buscar_por_id(id).getPontuacao(); }

    public List<Student> listar() { return studentRepository.findAll(); }

    public Student buscar_perfilId(Long id) { return studentRepository.findById(id).orElseThrow(() -> new RuntimeException(("Erro ao buscar usuario"))); }

    public Student buscar_perfilEmail(String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new org.springframework.web.server.ResponseStatusException(
                        org.springframework.http.HttpStatus.NOT_FOUND,
                        "Usuário não encontrado"
                ));

        Student student = user.getStudent();
        if (student == null) {
            throw new org.springframework.web.server.ResponseStatusException(
                    org.springframework.http.HttpStatus.NOT_FOUND,
                    "Perfil de estudante não vinculado ao usuário"
            );
        }

        return student;
    }

    @Transactional
    public void atualizar_perfil(Long id, UserUpdateStudentRequestDTO dto) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario não encontrado"));

        if (dto.nome() != null) {
            student.setNome(dto.nome());
        }
        if (dto.semestre() != null) {
            student.setSemestre(dto.semestre());
        }
        if (dto.bio() != null) {
            student.setBio(dto.bio());
        }
        if (dto.foto() != null) {
            student.setFoto(dto.foto());
        }

        studentRepository.save(student);
    }

    //mexe aqui isso que ta faltando
    public void trocarCurso(Long studentId, Long courseId) {

        LocalDateTime inicioMes = LocalDate.now()
                .withDayOfMonth(1)
               // .toLocalDate()
                .atStartOfDay();

        int trocas = courseChangeRepository.countByStudentIdAndDataTrocaAfter(studentId, inicioMes);

        if (trocas >= 6) {
            throw new RuntimeException("Limite de trocas atingido");
        }
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Aluno não encontrado"));

        Course novoCurso = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Curso não encontrado"));

        student.setCursoAtual(novoCurso);
        studentRepository.save(student);

        CourseChange change = new CourseChange();
        change.setStudent(student);
        change.setNovoCourse(novoCurso);
        change.setDataTroca(LocalDateTime.now());

        courseChangeRepository.save(change);
    }


    public void deletar(Long id){ studentRepository.deleteById(id);  }

    public UserLoginDataDTO obter_dados_login(Long id){
        User user = userRepository.findByStudent_Id(id)
                .orElseThrow(() -> new RuntimeException("Erro ao encontrar estudante"));

        return new UserLoginDataDTO(user.getEmail(), user.getSenha());
    }
}
