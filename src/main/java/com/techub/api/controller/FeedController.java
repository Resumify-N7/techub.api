package com.techub.api.controller;

import com.techub.api.domain.Student;
import com.techub.api.dto.FeedDTO;
import com.techub.api.service.CurrentUserService;
import com.techub.api.service.FeedService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/feed")
public class FeedController {

    @Autowired
    private FeedService feedService;

    @Autowired
    private CurrentUserService currentUserService;


    @GetMapping("/me")
    public ResponseEntity<FeedDTO> getFeed(
            @RequestParam(defaultValue = "0")
            int page,

            @RequestParam(defaultValue = "10")
            int size
    ) {
        Student student = currentUserService.getCurrentStudent();

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