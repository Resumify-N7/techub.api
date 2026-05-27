package com.techub.api.seed;

import com.techub.api.domain.Course;
import com.techub.api.domain.University;
import com.techub.api.repository.CourseRepository;
import com.techub.api.repository.UniversityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class CourseSeedRunner implements CommandLineRunner {

    private final CourseRepository courseRepository;

    private final UniversityRepository universityRepository;

    public CourseSeedRunner(
            CourseRepository courseRepository,
            UniversityRepository universityRepository
    ){
        this.courseRepository = courseRepository;
        this.universityRepository = universityRepository;
    }

    @Override @Transactional
    public void run(String... args)  {
        University univ = universityRepository.findById(1L)
                .orElseThrow(()-> new RuntimeException("Erro ao buscar universidade"));
        String baseCourse = "DSM";
        String baseCourseDescription = "Desenvolvimento de Software Multiplataforma!";

        if(!courseRepository.existsByNameIgnoreCase(baseCourse)){
            Course course = new Course();
            course.setUniversity(univ);
            course.setName(baseCourse);
            course.setDescricao(baseCourseDescription);
            courseRepository.save(course);
        }
    }
}
