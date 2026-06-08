package com.techub.api.controller;

import com.techub.api.domain.User;
import com.techub.api.dto.AuthResponse;
import com.techub.api.dto.UserLoginDataDTO;
import com.techub.api.dto.UserLoginResponse;
import com.techub.api.dto.UserLogoutResponse;
import com.techub.api.service.AuthenticationService;
import com.techub.api.service.CurrentUserService;
import com.techub.api.service.JwtService;
import com.techub.api.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthenticationService authenticationService;
    private final JwtService jwtService;
    private final UserService userService;
    private final CurrentUserService currentUserService;

    public AuthController(
            AuthenticationService authenticationService,
            JwtService jwtService,
            UserService userService,
            CurrentUserService currentUserService
    ) {
        this.authenticationService = authenticationService;
        this.jwtService = jwtService;
        this.userService = userService;
        this.currentUserService = currentUserService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UserLoginDataDTO dto, HttpServletResponse response) {
        User user = authenticationService.authentication(dto);
        String token = jwtService.generateToken(user.getEmail());

        response.addHeader("Set-Cookie",
                "accessToken=" + token +
                        "; HttpOnly" +
                        "; Path=/" +
                        "; Max-Age=3600" +
                        "; Secure" +
                        "; SameSite=None"
        );

        return ResponseEntity.ok(new UserLoginResponse("Sucesso ao criar o token", token));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletResponse response) {
        response.addHeader("Set-Cookie",
                "accessToken=" + "" +
                        "; HttpOnly" +
                        "; Path=/" +
                        "; Max-Age=0" +
                        "; Secure" +
                        "; SameSite=None"
        );

        return ResponseEntity.ok(new UserLogoutResponse("Sucesso ao fazer loogut"));
    }

    @GetMapping
    public ResponseEntity<?> auth() {
        User user = currentUserService.getCurrentUser();

        Long studentId   = user.getStudent()   != null ? user.getStudent().getId()   : null;
        Long professorId = user.getProfessor()  != null ? user.getProfessor().getId() : null;

        return ResponseEntity.ok(new AuthResponse(true, user.getId(), studentId, professorId, user.getRole()));
    }
}