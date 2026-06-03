package com.techub.api.controller;

import com.techub.api.dto.FollowesGetResponseDTO;
import com.techub.api.service.FollowService;
import com.techub.api.service.CurrentUserService;
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
    public ResponseEntity<List<FollowesGetResponseDTO>> myFollowing(@PathVariable Long id, @RequestParam(defaultValue = "20") int limit) {
        List<FollowesGetResponseDTO> following = followService.getFollowingDetails(id, limit);
        return ResponseEntity.ok(following);
    }

    @GetMapping("/followers/{id}")
    public ResponseEntity<List<FollowesGetResponseDTO>> getFollowers(@PathVariable Long id, @RequestParam(defaultValue = "20") int limit) {
        List<FollowesGetResponseDTO> followers = followService.getFollowersDetails(id, limit);
        return ResponseEntity.ok(followers);
    }

    @GetMapping("/following/me")
    public ResponseEntity<List<FollowesGetResponseDTO>> getFollowing(@RequestParam(defaultValue = "20") int limit) {
        Student me = currentUserService.getCurrentStudent();

        List<FollowesGetResponseDTO> following = followService.getFollowingDetails(me.getId(), limit);
        return ResponseEntity.ok(following);
    }

    @GetMapping("/followers/me")
    public ResponseEntity<List<FollowesGetResponseDTO>> myFollowers(@RequestParam(defaultValue = "20") int limit) {
        Student me = currentUserService.getCurrentStudent();

        List<FollowesGetResponseDTO> followers = followService.getFollowersDetails(me.getId(), limit);
        return ResponseEntity.ok(followers);
    }
}
