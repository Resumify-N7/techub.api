package com.techub.api.seed;

import com.techub.api.domain.Course;
import com.techub.api.domain.Student;
import com.techub.api.domain.University;
import com.techub.api.domain.User;
import com.techub.api.dto.SummaryCreateRequestDTO;
import com.techub.api.dto.UserCreateStudentRequestDTO;
import com.techub.api.repository.CourseChangeRepository;
import com.techub.api.repository.CourseRepository;
import com.techub.api.repository.FollowersRepository;
import com.techub.api.repository.LikesRepository;
import com.techub.api.repository.ReportRepository;
import com.techub.api.repository.StudentRepository;
import com.techub.api.repository.SubjectRepository;
import com.techub.api.repository.SummaryRepository;
import com.techub.api.repository.UniversityRepository;
import com.techub.api.repository.UserRepository;
import com.techub.api.service.FollowService;
import com.techub.api.service.SummaryService;
import com.techub.api.service.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.PageRequest;

import java.util.ArrayList;
import java.util.List;

@Component
@Profile("test")
public class TestSeedRunner implements CommandLineRunner {

    private final UserService userService;
    private final FollowService followService;
    private final SummaryService summaryService;
    private final UserRepository userRepository;
    private final StudentRepository studentRepository;
    private final FollowersRepository followersRepository;
    private final LikesRepository likesRepository;
    private final ReportRepository reportRepository;
    private final SummaryRepository summaryRepository;
    private final CourseChangeRepository courseChangeRepository;
    private final CourseRepository courseRepository;
    private final SubjectRepository subjectRepository;
    private final UniversityRepository universityRepository;

    public TestSeedRunner(
            UserService userService,
            FollowService followService,
            SummaryService summaryService,
            UserRepository userRepository,
            StudentRepository studentRepository,
            FollowersRepository followersRepository,
            LikesRepository likesRepository,
            ReportRepository reportRepository,
            SummaryRepository summaryRepository,
            CourseChangeRepository courseChangeRepository,
            CourseRepository courseRepository,
            SubjectRepository subjectRepository,
            UniversityRepository universityRepository) {
        this.userService = userService;
        this.followService = followService;
        this.summaryService = summaryService;
        this.userRepository = userRepository;
        this.studentRepository = studentRepository;
        this.followersRepository = followersRepository;
        this.likesRepository = likesRepository;
        this.reportRepository = reportRepository;
        this.summaryRepository = summaryRepository;
        this.courseChangeRepository = courseChangeRepository;
        this.courseRepository = courseRepository;
        this.subjectRepository = subjectRepository;
        this.universityRepository = universityRepository;
    }

    @Override
    @Transactional
    public void run(String... args) {
        resetDatabase();

        University university = createUniversity("Fatec Itaquera");
        createCourse("DSM", university);

        List<User> users = new ArrayList<>();
        for (int i = 1; i <= 4; i++) {
            users.add(createStudent(
                    "Student " + i,
                        "student" + i + "@teste.com",
                    "123456",
                    i
            ));
        }

        List<Student> students = users.stream()
                .map(User::getStudent)
                .toList();

        for (Student follower : students) {
            for (Student following : students) {
                if (!follower.getId().equals(following.getId())) {
                    followService.follow(follower.getId(), following.getId());
                }
            }
        }

        for (int i = 0; i < students.size(); i++) {
            Student student = students.get(i);
            for (int j = 1; j <= 2; j++) {
            var subject = subjectRepository.findByCourseAndSemestre(
                student.getCourse(),
                student.getSemestre(),
                PageRequest.of(0, 1)
            ).getContent().stream().findFirst()
                .orElseThrow(() -> new RuntimeException("Matéria não encontrada para o seed"));

                summaryService.saveSummary(
                        new SummaryCreateRequestDTO(
                                "summary" + (i + 1) + "Title" + j,
                    "summaryContent" + (i + 1) + j + " para o student" + (i + 1) + ".",
                    subject.getId()
                        ),
                        student.getId()
                );
            }
        }
    }

    private void resetDatabase() {
        reportRepository.deleteAll();
        likesRepository.deleteAll();
        summaryRepository.deleteAll();
        followersRepository.deleteAll();
        courseChangeRepository.deleteAll();
        userRepository.deleteAll();
        studentRepository.deleteAll();
        courseRepository.deleteAll();
        universityRepository.deleteAll();
    }

    private University createUniversity(String name) {
        University university = new University();
        university.setName(name);
        return universityRepository.save(university);
    }

    private Course createCourse(String name, University university) {
        Course course = new Course();
        course.setName(name);
        course.setUniversity(university);
        return courseRepository.save(course);
    }

    private User createStudent(String nome, String email, String senha, Integer semestre) {
        return userService.cadastrarAluno(new UserCreateStudentRequestDTO(
                nome,
                email,
                senha,
                semestre,
                null,
                null
        ));
    }
}
