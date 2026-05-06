package com.techub.api.service;

import com.techub.api.domain.Course;
import com.techub.api.domain.Role;
import com.techub.api.domain.Student;
import com.techub.api.domain.User;
import com.techub.api.dto.UserCreateStudentRequestDTO;
import com.techub.api.dto.UserRoleResponse;
import com.techub.api.dto.UserLoginDataDTO;
import com.techub.api.exception.EmailAlredyExistsExeception;
import com.techub.api.repository.CourseRepository;
import com.techub.api.repository.UserRepository;
import jakarta.validation.constraints.Email;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private CourseRepository courseRepository;

    public User criarUsuario(User user) {

        // valida email
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new EmailAlredyExistsExeception();
        }

        // hash da senha
        String senhaHash = passwordEncoder.encode(user.getSenha());
        user.setSenha(senhaHash);

        // salva
        return userRepository.save(user);
    }

    @Transactional
    public User cadastrarAluno(UserCreateStudentRequestDTO dto){
        User user = new User();
        user.setEmail(dto.email());
        user.setSenha(dto.senha());

        Student student = new Student();
        student.setNome(dto.nome());
        student.setBio(dto.bio());
        student.setFoto(dto.foto());
        student.setSemestre(dto.semestre());

        Course course = courseRepository.findTopByOrderByIdAsc()
                .orElseThrow(() -> new RuntimeException("Curso padrão não encontrado"));

        student.setCursoAtual(course);
        user.setStudent(student);
        user.setRole(Role.ALUNO);

        return criarUsuario(user);
    }

    public void atualizar_dados_login(Long id, UserLoginDataDTO dto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(("Erro ao buscar usuario!")));

        if(dto.email() != null) { user.setEmail(dto.email()); }
        if(dto.senha() != null) {  user.setSenha(passwordEncoder.encode(dto.senha())); }

        userRepository.save(user);
    }

    public List<User> listar() {
        return userRepository.findAll();
    }

    public Optional<User> buscar_por_id(Long id) {
        return userRepository.findById(id);
    }

    public Optional<User> buscar_por_email(String email) {
        return userRepository.findByEmail(email);
    }

    public void deletar(Long id) {
        userRepository.deleteById(id);
    }

    public UserRoleResponse descobrirRole(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(("Erro ao buscar usuario!")));

        if(user.getStudent() != null) {
            return new UserRoleResponse(user.getStudent().getId(), user.getRole());
        }
        throw new RuntimeException("Não foi possivel encontrar o role");
    }

}
