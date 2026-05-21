package com.techub.api.controller;

import com.techub.api.domain.Student;
import com.techub.api.dto.FeedDTO;
import com.techub.api.service.FeedService;
import com.techub.api.service.JwtService;
import com.techub.api.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/feed")
public class FeedController {

    @Autowired
    private FeedService feedService;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private StudentService studentService;


    @GetMapping("/me")
    public ResponseEntity<FeedDTO> getFeed(
            @CookieValue(name = "accessToken", required = false) String token,

            @RequestParam(defaultValue = "0")
            int page,

            @RequestParam(defaultValue = "10")
            int size
    ) {
        if(token == null || token.isBlank()){
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Token invalido"
            );
        }

        String email = jwtService.extractEmail(token);
        Student student = studentService.buscar_perfilEmail(email);

        FeedDTO feed = feedService.getFeed(student.getId(), page, size);
        return ResponseEntity.ok(feed);
    }

    @GetMapping("/filter")
    public ResponseEntity<FeedDTO> getFilteredFeed(
            @RequestParam(required = false) Long universityId,
            @RequestParam(required = false) Long courseId,
            @RequestParam(required = false) Long tagId,
            @RequestParam(required = false) Integer semestre,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        FeedDTO feed = feedService.getFilteredFeed(
                universityId, courseId, tagId, semestre, page, size
        );
        return ResponseEntity.ok(feed);
    }
}