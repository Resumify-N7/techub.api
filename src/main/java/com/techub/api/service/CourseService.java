package com.techub.api.service;

import com.techub.api.domain.Course;
import com.techub.api.repository.CourseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CourseService {

    @Autowired
    private CourseRepository courseRepository;

    public Course criar(Course course) {
        return courseRepository.save(course);
    }

    public List<Course> listar() {
        return courseRepository.findAll();
    }
}