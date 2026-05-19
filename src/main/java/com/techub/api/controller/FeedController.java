package com.techub.api.controller;

import com.techub.api.dto.FeedDTO;
import com.techub.api.service.FeedService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/feed")
public class FeedController {

    @Autowired
    private FeedService feedService;


    @GetMapping("/{studentId}")
    public ResponseEntity<FeedDTO> getFeed(

            @PathVariable Long studentId, // apenas o studante pode seguir ou ser seguido!

            @RequestParam(defaultValue = "0")
            int page,

            @RequestParam(defaultValue = "10")
            int size
    ) {

        FeedDTO feed = feedService.getFeed(studentId, page, size);

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