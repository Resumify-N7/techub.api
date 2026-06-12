package com.techub.api.service;

import com.techub.api.domain.*;
import com.techub.api.dto.*;
import com.techub.api.email.EmailSender;
import com.techub.api.email.EmailTemplate;
import com.techub.api.exception.EmailAlreadyExistsException;
import com.techub.api.repository.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final ADMRepository admRepository;
    private final PasswordEncoder passwordEncoder;
    private final CourseRepository courseRepository;
    private final AvatarService avatarService;
    private final EmailSender emailSender;
    private final EmailTemplate emailTemplate;
    private final CurrentUserService currentUserService;
    private final JwtService jwtService;
    private final SubjectRepository subjectRepository;

    @Value("${app.frontend-url:http://localhost:5173}")
    private String frontendUrl;

    public UserService(
            UserRepository userRepository,
            ADMRepository admRepository,
            PasswordEncoder passwordEncoder,
            CourseRepository courseRepository,
            AvatarService avatarService,
            EmailSender emailSender,
            EmailTemplate emailTemplate,
            CurrentUserService currentUserService,
            JwtService jwtService,
            SubjectRepository subjectRepository
    ) {
        this.userRepository = userRepository;
        this.admRepository = admRepository;
        this.passwordEncoder = passwordEncoder;
        this.courseRepository = courseRepository;
        this.avatarService = avatarService;
        this.emailSender = emailSender;
        this.emailTemplate = emailTemplate;
        this.currentUserService = currentUserService;
        this.jwtService = jwtService;
        this.subjectRepository = subjectRepository;
    }

    public User criarUsuario(User user, boolean passwordAlreadyHashed) {
        user.setAtivo(true);

        if (userRepository.existsByEmail(user.getEmail())) {
            throw new EmailAlreadyExistsException();
        }

        if (!passwordAlreadyHashed) {
            user.setSenha(passwordEncoder.encode(user.getSenha()));
        }

        return userRepository.save(user);
    }

    @Transactional
    public void sendEmailForPedingRegistrationStudent(PendingStudentRegistrationDTO dto) {
        emailSender.validarDominioInstitucional(dto.email());

        if (userRepository.existsByEmail(dto.email())) {
            throw new EmailAlreadyExistsException();
        }

        String senhaHash = passwordEncoder.encode(dto.senha());

        String token = jwtService.generatePendingStudentRegistration(
                new PendingStudentRegistrationDTO(dto.nome(), dto.email(), senhaHash, dto.semestre())
        );

        String confirmationLink = frontendUrl + "/confirm-email/student?token=" + token;

        emailSender.send(
                dto.email(),
                "Confirme seu cadastro no Resumify",
                emailTemplate.confirmationTemplate(dto.nome(), confirmationLink, "15 minutos")
        );
    }

    @Transactional
    public User cadastrarAlunoViaConfirmacaoEmail(UserCreateStudentRequestDTO dto) {
        if (userRepository.existsByEmail(dto.email())) {
            throw new EmailAlreadyExistsException();
        }

        User user = new User();
        user.setEmail(dto.email());
        user.setSenha(dto.senha());

        Student student = new Student();
        student.setNome(dto.nome());
        student.setBio(dto.bio());
        student.setSemestre(dto.semestre());

        if (dto.avatarUrl() != null && !dto.avatarUrl().isBlank()) {
            student.setAvatar(avatarService.buscarPorUrl(dto.avatarUrl()));
        } else {
            student.setAvatar(avatarService.getOrCreateDefault());
        }

        Course course = courseRepository.findTopByOrderByIdAsc()
                .orElseThrow(() -> new RuntimeException("Curso padrão não encontrado no banco. Execute o seed de cursos."));

        student.setCourse(course);
        user.setStudent(student);
        user.setRole(Role.ALUNO);

        return criarUsuario(user, true);
    }

    @Transactional
    public User cadastrarAluno(UserCreateStudentRequestDTO dto) {
        User currentUser = currentUserService.getCurrentUser();

        if (currentUser.getRole() != Role.ADM) {
            throw new RuntimeException("Acesso negado a essa rota!");
        }

        User user = new User();
        user.setEmail(dto.email());
        user.setSenha(dto.senha());

        Student student = new Student();
        student.setNome(dto.nome());
        student.setBio(dto.bio());
        student.setSemestre(dto.semestre());

        if (dto.avatarUrl() != null && !dto.avatarUrl().isBlank()) {
            student.setAvatar(avatarService.buscarPorUrl(dto.avatarUrl()));
        } else {
            student.setAvatar(avatarService.getOrCreateDefault());
        }

        Course course = courseRepository.findTopByOrderByIdAsc()
                .orElseThrow(() -> new RuntimeException("Curso padrão não encontrado no banco."));

        student.setCourse(course);
        user.setStudent(student);
        user.setRole(Role.ALUNO);

        return criarUsuario(user, false);
    }

    @Transactional
    public void sendEmailForPedingRegistrationProfessor(PendingProfessorRegistrationDTO dto) {
        emailSender.validarDominioInstitucional(dto.email());

        if (userRepository.existsByEmail(dto.email())) {
            throw new EmailAlreadyExistsException();
        }

        if (dto.subjectId() == null) {
            throw new IllegalArgumentException("Matéria é obrigatória para o convite de professor.");
        }
        subjectRepository.findById(dto.subjectId())
                .orElseThrow(() -> new RuntimeException("Matéria não encontrada."));

        String senhaHash = passwordEncoder.encode(dto.senha());
        String token = jwtService.generatePendingProfessorRegistration(
                new PendingProfessorRegistrationDTO(dto.nome(), dto.email(), senhaHash, dto.subjectId())
        );

        String confirmationLink = frontendUrl + "/confirm-email/professor?token=" + token;

        emailSender.send(
                dto.email(),
                "Convite para o Resumify — você foi convidado como professor",
                emailTemplate.confirmationTemplate(dto.nome(), confirmationLink, "2 dias")
        );
    }

    @Transactional
    public User cadastrarProfessorViaConfirmacaoEmail(PendingProfessorRegistrationDTO dto) {
        if (userRepository.existsByEmail(dto.email())) {
            throw new EmailAlreadyExistsException();
        }

        Subject subject = subjectRepository.findById(dto.subjectId())
                .orElseThrow(() -> new RuntimeException("Matéria não encontrada."));

        User user = new User();
        user.setEmail(dto.email());
        user.setSenha(dto.senha());

        Professor professor = new Professor();
        professor.setNome(dto.nome());
        professor.setSubject(subject);
        professor.setAvatar(avatarService.getOrCreateDefault());

        user.setProfessor(professor);
        user.setRole(Role.PROFESSOR);

        return criarUsuario(user, true);
    }

    @Transactional
    public User cadastrarADM(ADMCreateRequestDTO dto) {
        User user = new User();
        user.setEmail(dto.email());
        user.setSenha(dto.senha());

        ADM adm = new ADM();
        adm.setUsername(dto.username());

        ADM admSalvo = admRepository.save(adm);

        user.setAdm(admSalvo);
        user.setRole(Role.ADM);
        return criarUsuario(user, false);
    }

    public void atualizar_dados_login(Long id, UserLoginDataDTO dto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Erro ao buscar usuario!"));

        if (dto.email() != null) { user.setEmail(dto.email()); }
        if (dto.senha() != null) { user.setSenha(passwordEncoder.encode(dto.senha())); }

        userRepository.save(user);
    }

    public List<UserGetResponseDTO> findByAtivoTrue(int limit) {
        int pageSize = Math.max(1, limit);
        return userRepository.findActive(PageRequest.of(0, pageSize))
                .getContent()
                .stream()
                .map(user -> new UserGetResponseDTO(
                        user.getId(),
                        Optional.ofNullable(user.getStudent()).map(Student::getId).orElse(null),
                        user.getEmail(),
                        user.getRole(),
                        user.getCreatedAt(),
                        user.getAtivo()
                )).toList();
    }

    public List<UserGetResponseDTO> findByAtivoFalse(int limit) {
        int pageSize = Math.max(1, limit);
        return userRepository.findInactive(PageRequest.of(0, pageSize))
                .getContent()
                .stream()
                .map(user -> new UserGetResponseDTO(
                        user.getId(),
                        Optional.ofNullable(user.getStudent()).map(Student::getId).orElse(null),
                        user.getEmail(),
                        user.getRole(),
                        user.getCreatedAt(),
                        user.getAtivo()
                )).toList();
    }

    public Optional<User> buscar_por_id(Long id) {
        return userRepository.findById(id);
    }

    public Optional<User> buscar_por_email(String email) {
        return userRepository.findByEmail(email);
    }

    public void deletar(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Erro ao buscar usuario!"));
        user.setAtivo(false);
        userRepository.save(user);
    }

    @Transactional
    public void atualizar_status(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Erro ao procurar usuario"));
        user.setAtivo(!user.getAtivo());
        userRepository.save(user);
    }

    @Transactional
    public void atualizar_status_por_student_id(Long studentId) {
        User user = userRepository.findByStudent_Id(studentId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado para o studentId: " + studentId));
        user.setAtivo(!user.getAtivo());
        userRepository.save(user);
    }

    public UserRoleResponse descobrirRole(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Erro ao buscar usuario!"));

        if (user.getStudent() != null) {
            return new UserRoleResponse(user.getStudent().getId(), user.getRole());
        }
        throw new RuntimeException("Não foi possível encontrar o role para este usuário");
    }
}