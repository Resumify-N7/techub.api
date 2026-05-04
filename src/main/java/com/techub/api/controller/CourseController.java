package com.techub.api.controller;

import com.techub.api.domain.Course;
import com.techub.api.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/courses")
public class CourseController {

    @Autowired
    private CourseService courseService;

    @PostMapping
    public ResponseEntity<Course> criar(@RequestBody Course course) {
        return ResponseEntity.ok(courseService.criar(course));
    }

    @GetMapping
    public ResponseEntity<List<Course>> listar() {
        return ResponseEntity.ok(courseService.listar());
    }
}