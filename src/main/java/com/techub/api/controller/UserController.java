package com.techub.api.controller;

import com.techub.api.domain.User;
import com.techub.api.dto.*;
import com.techub.api.exception.EmailAlredyExistsExeception;
import com.techub.api.exception.TokenExpiradoException;
import com.techub.api.repository.UserRepository;
import com.techub.api.service.JwtService;
import com.techub.api.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;
    private final JwtService jwtService;
    private final UserRepository userRepository;

    public UserController(UserService userService, JwtService jwtService, UserRepository userRepository) {
        this.userService = userService;
        this.jwtService = jwtService;
        this.userRepository = userRepository;
    }

    @PostMapping("/confirm/email")
    public ResponseEntity<?> iniciarCadastroPendente(@Valid @RequestBody PendingStudentRegistrationDTO dto) {
        userService.sendEmailForPedingRegistrationStudent(dto);
        return ResponseEntity.ok("Email de confirmação enviado");
    }

    @PostMapping("/email")
    public ResponseEntity<UserLoginResponse> confirmarEmail(
            @RequestParam String token,
            HttpServletResponse response
    ) {
        try {
            PendingStudentRegistrationDTO dto = jwtService.extractPendingStudentRegistration(token);
            userService.cadastrarAlunoViaConfirmacaoEmail(
                    new UserCreateStudentRequestDTO(
                            dto.nome(),
                            dto.email(),
                            dto.senha(),
                            dto.semestre(),
                            null,
                            null
                    )
            );

            String sessionToken = jwtService.generateToken(dto.email());
            response.addHeader("Set-Cookie",
                    "accessToken=" + sessionToken
                            + "; HttpOnly"
                            + "; Path=/"
                            + "; Max-Age=3600"
                            + "; Secure"
                            + "; SameSite=None"
            );

            return ResponseEntity.ok(new UserLoginResponse("Sucesso ao criar o token", sessionToken));
        } catch (TokenExpiradoException e) {
            String email = jwtService.extractEmailFromExpiredToken(token);
            if (email != null && userRepository.existsByEmail(email)) {
                throw new EmailAlredyExistsExeception();
            }
            throw e;
        }
    }

    @PostMapping
    public ResponseEntity<?> criarUsuarioAluno(@Valid @RequestBody UserCreateStudentRequestDTO dto) {
        User user = userService.cadastrarAluno(dto);
        return ResponseEntity.ok(new UserCreateResponseDTO("Usuario criado com sucesso", user.getId()));
    }

    @PostMapping("/adm")
    public ResponseEntity<?> criarUsuarioAdm(ADMCreateRequestDTO dto) {
        userService.cadastrarADM(dto);
        return ResponseEntity.ok("Sucesso ao criar ADM");
    }

    @GetMapping
    public List<UserGetResponseDTO> listarUser(@RequestParam(defaultValue = "20") int limit) {
        return userService.findByAtivoTrue(limit);
    }

    @GetMapping("/desativados")
    public List<UserGetResponseDTO> listarUserDesativados(@RequestParam(defaultValue = "20") int limit) {
        return userService.findByAtivoFalse(limit);
    }

    @GetMapping("/{id}")
    public User buscarUserPorId(@PathVariable Long id) {
        return userService.buscar_por_id(id).orElse(null);
    }

    @PutMapping("/{id}")
    public String atualizarDadosLogin(@PathVariable Long id, @RequestBody UserLoginDataDTO dto) {
        userService.atualizar_dados_login(id, dto);
        return "Dados atualizado com sucesso!";
    }

    @DeleteMapping("/{id}")
    public String deletarUserPorId(@PathVariable Long id) {
        userService.deletar(id);
        return "Dados apagados com sucesso!";
    }

    @PatchMapping("/atualizar_status/{id}")
    public ResponseEntity<?> atualizarStatus(@PathVariable Long id) {
        userService.atualizar_status(id);
        return ResponseEntity.ok("Sucesso ao ativar/desativar usuário");
    }

    @PatchMapping("/atualizar_status/student/{studentId}")
    public ResponseEntity<?> atualizarStatusPorStudentId(@PathVariable Long studentId) {
        userService.atualizar_status_por_student_id(studentId);
        return ResponseEntity.ok("Sucesso ao ativar/desativar usuário");
    }

    @GetMapping("/role/{id}")
    public UserRoleResponse descobrirRole(@PathVariable Long id) {
        return userService.descobrirRole(id);
    }
}