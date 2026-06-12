package com.techub.api.controller;

import com.techub.api.dto.FollowPageDTO;
import com.techub.api.service.FollowService;
import com.techub.api.service.CurrentUserService;
import com.techub.api.domain.Student;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/follow")
public class FollowersController {

    @Autowired
    private FollowService followService;

    @Autowired
    private CurrentUserService currentUserService;

    @PostMapping("/{targetStudentId}")
    public ResponseEntity<?> follow(@PathVariable Long targetStudentId) {
        Student me = currentUserService.getCurrentStudent();
        followService.follow(me.getId(), targetStudentId);
        return ResponseEntity.ok("Seguido");
    }

    @DeleteMapping("/{targetStudentId}")
    public ResponseEntity<?> unfollow(@PathVariable Long targetStudentId) {
        Student me = currentUserService.getCurrentStudent();
        followService.unfollow(me.getId(), targetStudentId);
        return ResponseEntity.ok("Deixou de seguir");
    }


    @GetMapping("/following/{id}")
    public ResponseEntity<FollowPageDTO> myFollowing(
            @PathVariable Long id,
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        return ResponseEntity.ok(followService.getFollowingDetails(id, page, size));
    }

    @GetMapping("/followers/{id}")
    public ResponseEntity<FollowPageDTO> getFollowers(
            @PathVariable Long id,
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        return ResponseEntity.ok(followService.getFollowersDetails(id, page, size));
    }

    @GetMapping("/following/me")
    public ResponseEntity<FollowPageDTO> getFollowing(
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        Student me = currentUserService.getCurrentStudent();
        return ResponseEntity.ok(followService.getFollowingDetails(me.getId(), page, size));
    }
    
    @GetMapping("/followers/me")
    public ResponseEntity<FollowPageDTO> myFollowers(
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        Student me = currentUserService.getCurrentStudent();
        return ResponseEntity.ok(followService.getFollowersDetails(me.getId(), page, size));
    }
}