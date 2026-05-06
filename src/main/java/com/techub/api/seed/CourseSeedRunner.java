package com.techub.api.seed;

import com.techub.api.domain.Course;
import com.techub.api.repository.CourseRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class CourseSeedRunner implements CommandLineRunner {

    private final CourseRepository courseRepository;

    public CourseSeedRunner(CourseRepository courseRepository){
        this.courseRepository = courseRepository;
    }

    @Override @Transactional
    public void run(String... args)  {
        String baseCourse = "DSM";
        String baseCourseDescription = "Desenvolvimento de Software Multiplataforma!";

        if(!courseRepository.existsByNameIgnoreCase(baseCourse)){
            Course course = new Course();
            course.setName(baseCourse);
            course.setDescricao(baseCourseDescription);
            courseRepository.save(course);
        }
    }
}
