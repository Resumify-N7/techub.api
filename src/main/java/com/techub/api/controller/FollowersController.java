package com.techub.api.controller;

import com.techub.api.dto.FollowesGetResponseDTO;
import com.techub.api.service.FollowService;
import com.techub.api.service.StudentService;
import com.techub.api.service.JwtService;
import com.techub.api.domain.Student;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/follow")
public class FollowersController {

    @Autowired
    private FollowService followService;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private StudentService studentService;

    @PostMapping("/{targetStudentId}")
    public ResponseEntity<?> follow(@CookieValue(name = "accessToken", required = false) String token,
                                    @PathVariable Long targetStudentId) {
        if (token == null || token.isBlank()) {
            return ResponseEntity.status(401).body("Token ausente");
        }

        String email = jwtService.extractEmail(token);
        Student me = studentService.buscar_perfilEmail(email);

        followService.follow(me.getId(), targetStudentId);
        return ResponseEntity.ok("Seguido");
    }

    @DeleteMapping("/{targetStudentId}")
    public ResponseEntity<?> unfollow(@CookieValue(name = "accessToken", required = false) String token,
                                      @PathVariable Long targetStudentId) {
        if (token == null || token.isBlank()) {
            return ResponseEntity.status(401).body("Token ausente");
        }

        String email = jwtService.extractEmail(token);
        Student me = studentService.buscar_perfilEmail(email);

        followService.unfollow(me.getId(), targetStudentId);
        return ResponseEntity.ok("Deixou de seguir");
    }

    @GetMapping("/following/me")
    public ResponseEntity<List<FollowesGetResponseDTO>> myFollowing(@CookieValue(name = "accessToken", required = false) String token) {
        if (token == null || token.isBlank()) {
            return ResponseEntity.status(401).build();
        }

        String email = jwtService.extractEmail(token);
        Student me = studentService.buscar_perfilEmail(email);

        List<FollowesGetResponseDTO> following = followService.getFollowingDetails(me.getId());
        return ResponseEntity.ok(following);
    }

    @GetMapping("/followers/me")
    public ResponseEntity<List<FollowesGetResponseDTO>> myFollowers(@CookieValue(name = "accessToken", required = false) String token) {
        if (token == null || token.isBlank()) {
            return ResponseEntity.status(401).build();
        }

        String email = jwtService.extractEmail(token);
        Student me = studentService.buscar_perfilEmail(email);

        List<FollowesGetResponseDTO> followers = followService.getFollowersDetails(me.getId());
        return ResponseEntity.ok(followers);
    }
}
