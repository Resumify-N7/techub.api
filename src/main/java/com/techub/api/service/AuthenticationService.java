package com.techub.api.service;

import com.techub.api.domain.User;
import com.techub.api.dto.UserLoginDataDTO;
import com.techub.api.email.EmailSender;
import com.techub.api.email.EmailTemplate;
import com.techub.api.exception.InvalidCredentialsException;
import com.techub.api.exception.UserDesactivatedException;
import com.techub.api.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {

    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final EmailSender emailSender;
    private final EmailTemplate emailTemplate;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.frontend-url}")
    private String frontendUrl;

    public AuthenticationService(
            UserRepository userRepository,
            AuthenticationManager authenticationManager,
            JwtService jwtService,
            EmailSender emailSender,
            EmailTemplate emailTemplate,
            PasswordEncoder passwordEncoder
    ) {
        this.userRepository = userRepository;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.emailSender = emailSender;
        this.emailTemplate = emailTemplate;
        this.passwordEncoder = passwordEncoder;
    }

    public User authentication(UserLoginDataDTO input) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            input.email(),
                            input.senha()
                    )
            );
        } catch (DisabledException ex) {
            throw new UserDesactivatedException();
        } catch (AuthenticationException ex) {
            throw new InvalidCredentialsException();
        }

        User user = userRepository.findByEmail(input.email())
                .orElseThrow(InvalidCredentialsException::new);

        if (user.getAtivo().equals(Boolean.FALSE)) {
            throw new UserDesactivatedException();
        }

        return user;
    }

    public void solicitarRedefinicao(String email) {
        // Se o email não existir, retorna silenciosamente (evita enumeração de usuários)
        userRepository.findByEmail(email).ifPresent(user -> {
            String token = jwtService.generatePasswordResetToken(email);
            String link = frontendUrl + "/redefinir-senha?token=" + token;
            String html = emailTemplate.passwordResetTemplate(user.getUsername(), link);
            emailSender.send(email, "Redefinição de senha - Resumify", html);
        });
    }

    public void redefinirSenha(String token, String novaSenha) {
        String email = jwtService.extractEmailFromPasswordResetToken(token);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado."));

        user.setSenha(passwordEncoder.encode(novaSenha));
        userRepository.save(user);
    }
}
