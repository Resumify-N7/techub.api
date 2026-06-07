package com.techub.api.controller;

import com.techub.api.domain.ADM;
import com.techub.api.domain.User;
import com.techub.api.dto.*;
import com.techub.api.exception.EmailAlredyExistsExeception;
import com.techub.api.exception.TokenExpiradoException;
import com.techub.api.repository.UserRepository;
import com.techub.api.service.ADMService;
import com.techub.api.service.JwtService;
import com.techub.api.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/adm")
public class ADMController {

    private final ADMService admService;
    private final UserService userService;
    private final UserRepository userRepository;
    private final JwtService jwtService;

    public ADMController(
            ADMService admService,
            UserService userService,
            UserRepository userRepository,
            JwtService jwtService
    ) {
        this.admService = admService;
        this.userService = userService;
        this.userRepository = userRepository;
        this.jwtService = jwtService;
    }

    @GetMapping
    public List<ADM> listar(@RequestParam(defaultValue = "20") int limit) {
        return admService.listar_adm(limit);
    }

    @PostMapping("/confirm/email/professor")
    public ResponseEntity<?> enviarConviteProfessor(
            @Valid @RequestBody PendingProfessorRegistrationDTO dto
    ) {
        userService.sendEmailForPedingRegistrationProfessor(dto);
        return ResponseEntity.ok("Convite enviado para " + dto.email());
    }

    @PostMapping("/professor")
    public ResponseEntity<UserLoginResponse> confirmarProfessor(
            @RequestParam String token,
            HttpServletResponse response
    ) {
        try {
            PendingProfessorRegistrationDTO dto = jwtService.extractPendingProfessorRegistration(token);
            userService.cadastrarProfessorViaConfirmacaoEmail(dto);

            String sessionToken = jwtService.generateToken(dto.email());
            response.addHeader("Set-Cookie",
                    "accessToken=" + sessionToken
                            + "; HttpOnly"
                            + "; Path=/"
                            + "; Max-Age=3600"
                            + "; Secure"
                            + "; SameSite=None"
            );

            return ResponseEntity.ok(new UserLoginResponse("Professor cadastrado com sucesso", sessionToken));

        } catch (TokenExpiradoException e) {
            String email = jwtService.extractEmailFromExpiredToken(token);
            if (email != null && userRepository.existsByEmail(email)) {
                throw new EmailAlredyExistsExeception();
            }
            throw e;
        }
    }
}