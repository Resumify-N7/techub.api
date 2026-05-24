package com.techub.api.service;

import com.techub.api.domain.Course;
import com.techub.api.repository.CourseRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CourseService {

    @Autowired
    private CourseRepository courseRepository;

    public Course criar(Course course) {
        if(!courseRepository.existsByNameIgnoreCase(course.getName())){
            return courseRepository.save(course);
        }

        throw new RuntimeException(("Nome de curso já usado"));
    }

    public List<Course> listar(int limit) {
        int pageSize = Math.max(1, limit);
        return courseRepository.findActive(PageRequest.of(0, pageSize)).getContent();
    }
}