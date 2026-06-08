package com.techub.api.service;

import com.techub.api.domain.*;
import com.techub.api.dto.StudentGetDTO;
import com.techub.api.dto.UserLoginDataDTO;
import com.techub.api.dto.UserUpdateStudentRequestDTO;
import com.techub.api.repository.*;
import org.springframework.data.domain.PageRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

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

    @Autowired
    private AvatarService avatarService;

    @Autowired
    private FollowersRepository followersRepository;

    @Autowired
    private CurrentUserService currentUserService;

    public Student buscar_por_id(Long id) {
        return studentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario não encontrado"));
    }

    public Integer obter_pontuacao(Long id){ return resolveStudentByIdOrUserId(id).getPontuacao(); }

    public List<Student> listar(int limit) {
        int pageSize = Math.max(1, limit);
        return studentRepository.findActive(PageRequest.of(0, pageSize)).getContent();
    }

    public Student resolveStudentByIdOrUserId(Long id) {
        Optional<Student> studentOpt = studentRepository.findById(id);
        if (studentOpt.isPresent()) {
            return studentOpt.get();
        }

        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        org.springframework.http.HttpStatus.NOT_FOUND,
                        "Usuário não encontrado"
                ));

        Student student = user.getStudent();
        if (student == null) {
            throw new ResponseStatusException(
                    org.springframework.http.HttpStatus.NOT_FOUND,
                    "Perfil de estudante não vinculado ao usuário"
            );
        }

        return student;
    }

    public StudentGetDTO buscar_perfilId(Long id) {
        Student student = buscar_por_id(id);
        User currentUser = currentUserService.getCurrentUser();

        Boolean seguindoCurrentUser = null;
        Boolean seguidoPeloCurrentUser = null;

        if (currentUser.getRole() == Role.ALUNO) {
            seguidoPeloCurrentUser = followersRepository
                    .findByFollowerIdAndFollowingId(
                            currentUser.getStudent().getId(),
                            student.getId())
                    .isPresent();

            seguindoCurrentUser = followersRepository
                    .findByFollowerIdAndFollowingId(
                            student.getId(),
                            currentUser.getStudent().getId())
                    .isPresent();
        }

        return new StudentGetDTO(
                student.getAtivo(),
                student.getId(),
                student.getNome(),
                student.getSemestre(),
                student.getBio(),
                student.getAvatar(),
                student.getCourse(),
                student.getPontuacao(),
                seguindoCurrentUser,
                seguidoPeloCurrentUser,
                studentRepository.countFollowers(student.getId()),
                studentRepository.countFollowing(student.getId())
        );
    }

    public StudentGetDTO buscar_perfilIdAsAdmin(Long id) {
        Student student = buscar_por_id(id);

        return new StudentGetDTO(
                student.getAtivo(),
                student.getId(),
                student.getNome(),
                student.getSemestre(),
                student.getBio(),
                student.getAvatar(),
                student.getCourse(),
                student.getPontuacao(),
                null,
                null,
                studentRepository.countFollowers(student.getId()),
                studentRepository.countFollowing(student.getId())
        );
    }

    public Student buscar_perfilEmail(String email) {

        User user = userRepository.findByEmailAndAtivoTrue(email)
                .orElseThrow(() -> new ResponseStatusException(
                        org.springframework.http.HttpStatus.NOT_FOUND,
                        "Usuário não encontrado"
                ));

        Student student = user.getStudent();
        if (student == null) {
            throw new ResponseStatusException(
                    org.springframework.http.HttpStatus.NOT_FOUND,
                    "Perfil de estudante não vinculado ao usuário"
            );
        }

        return student;
    }

    @Transactional
    public void atualizar_perfil(Long id, UserUpdateStudentRequestDTO dto) {
        Student student = resolveStudentByIdOrUserId(id);

        if (dto.nome() != null) {
            student.setNome(dto.nome());
        }
        if (dto.semestre() != null) {
            student.setSemestre(dto.semestre());
        }
        if (dto.bio() != null) {
            student.setBio(dto.bio());
        }
        if (dto.avatarUrl() != null && !dto.avatarUrl().isBlank()) {
            student.setAvatar(avatarService.buscarPorUrl(dto.avatarUrl()));
        }

        studentRepository.save(student);
    }


    //mexe aqui isso que ta faltando
    public void trocarCurso(Long studentId, Long courseId) {
        Student student = resolveStudentByIdOrUserId(studentId);

        LocalDateTime inicioMes = LocalDate.now()
                .withDayOfMonth(1)
               // .toLocalDate()
                .atStartOfDay();

        int trocas = courseChangeRepository.countByStudentIdAndDataTrocaAfter(student.getId(), inicioMes);

        if (trocas >= 6) {
            throw new RuntimeException("Limite de trocas atingido");
        }
        Course novoCurso = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Curso não encontrado"));

        student.setCourse(novoCurso);
        studentRepository.save(student);

        CourseChange change = new CourseChange();
        change.setStudent(student);
        change.setNovoCourse(novoCurso);
        change.setDataTroca(LocalDateTime.now());

        courseChangeRepository.save(change);
    }


    public void deletar(Long id){
        Student student = resolveStudentByIdOrUserId(id);
        student.setAtivo(false);
        studentRepository.save(student);
    }

    @Transactional
    public void atualizar_status(Long id) {
        Student student = resolveStudentByIdOrUserId(id);
        student.setAtivo(!Boolean.TRUE.equals(student.getAtivo()));
        studentRepository.save(student);
    }

    public UserLoginDataDTO obter_dados_login(Long id){
        Student student = resolveStudentByIdOrUserId(id);

        User user = userRepository.findByStudent_Id(student.getId())
                .orElseThrow(() -> new RuntimeException("Erro ao encontrar estudante"));

        return new UserLoginDataDTO(user.getEmail(), user.getSenha());
    }
}
