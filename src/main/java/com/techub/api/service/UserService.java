package com.techub.api.service;

import com.techub.api.domain.*;
import com.techub.api.dto.*;
import com.techub.api.email.EmailSender;
import com.techub.api.email.EmailTemplate;
import com.techub.api.exception.DominioEmailInvalidoException;
import com.techub.api.exception.EmailAlredyExistsExeception;
import com.techub.api.repository.ADMRepository;
import com.techub.api.repository.CourseRepository;
import com.techub.api.repository.UserRepository;
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
            JwtService jwtService
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
    }

    public User criarUsuario(User user, boolean passwordAlreadyHashed) {
        user.setAtivo(true);

        if (userRepository.existsByEmail(user.getEmail())) {
            throw new EmailAlredyExistsExeception();
        }

        if (!passwordAlreadyHashed) {
            user.setSenha(passwordEncoder.encode(user.getSenha()));
        }

        return userRepository.save(user);
    }

    @Transactional
    public void sendEmailForPedingRegistrationStudent(PendingStudentRegistrationDTO dto) {
        validarDominioInstitucional(dto.email());

        if (userRepository.existsByEmail(dto.email())) {
            throw new EmailAlredyExistsExeception();
        }

        String senhaHash = passwordEncoder.encode(dto.senha());

        String token = jwtService.generatePendingStudentRegistration(
                new PendingStudentRegistrationDTO(dto.nome(), dto.email(), senhaHash, dto.semestre())
        );

        String confirmationLink = frontendUrl + "/confirm-email?token=" + token;

        emailSender.send(
                dto.email(),
                "Confirme seu cadastro",
                emailTemplate.confirmationTemplate(dto.nome(), confirmationLink)
        );
    }

    @Transactional
    public User cadastrarAlunoViaConfirmacaoEmail(UserCreateStudentRequestDTO dto) {
        if (userRepository.existsByEmail(dto.email())) {
            throw new EmailAlredyExistsExeception();
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

    public UserRoleResponse descobrirRole(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Erro ao buscar usuario!"));

        if (user.getStudent() != null) {
            return new UserRoleResponse(user.getStudent().getId(), user.getRole());
        }
        throw new RuntimeException("Não foi possível encontrar o role para este usuário");
    }

    private static final List<String> DOMINIOS_PERMITIDOS = List.of(
            "@aluno.cps.sp.gov.br",
            "@fatec.sp.gov.br",
            "@cps.sp.gov.br"
    );

    private void validarDominioInstitucional(String email) {
        String emailLower = email.toLowerCase();
        boolean valido = DOMINIOS_PERMITIDOS.stream().anyMatch(emailLower::endsWith);
        if (!valido) {
            throw new DominioEmailInvalidoException();
        }
    }
}