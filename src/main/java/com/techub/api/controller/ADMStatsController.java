package com.techub.api.controller;

import com.techub.api.dto.DashboardStatsDTO;
import com.techub.api.repository.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/stats")
public class ADMStatsController {

    private final UserRepository userRepository;
    private final SummaryRepository summaryRepository;
    private final CourseRepository courseRepository;
    private final TagsRepository tagRepository;
    private final SubjectRepository subjectRepository;
    private final BadgeRepository badgeRepository;
    private final AvatarRepository avatarRepository;
    private final BioRepository bioRepository;
    private final UniversityRepository universityRepository;
    private final StudentRepository studentRepository;
    private final ADMRepository adminRepository;

    public ADMStatsController(
            UserRepository userRepository,
            SummaryRepository summaryRepository,
            CourseRepository courseRepository,
            TagsRepository tagRepository,
            SubjectRepository subjectRepository,
            BadgeRepository badgeRepository,
            AvatarRepository avatarRepository,
            BioRepository bioRepository,
            UniversityRepository universityRepository,
            StudentRepository studentRepository,
            ADMRepository adminRepository
    ) {
        this.userRepository = userRepository;
        this.summaryRepository = summaryRepository;
        this.courseRepository = courseRepository;
        this.tagRepository = tagRepository;
        this.subjectRepository = subjectRepository;
        this.badgeRepository = badgeRepository;
        this.avatarRepository = avatarRepository;
        this.bioRepository = bioRepository;
        this.universityRepository = universityRepository;
        this.studentRepository = studentRepository;
        this.adminRepository = adminRepository;
    }

    @GetMapping
    public ResponseEntity<DashboardStatsDTO> getStats() {
        return ResponseEntity.ok(new DashboardStatsDTO(
                userRepository.countByAtivoTrue(),
                userRepository.countByAtivoFalse(),
                summaryRepository.countByAtivoTrue(),
                summaryRepository.countByAtivoFalse(),
                courseRepository.countByAtivoTrue(),
                courseRepository.countByAtivoFalse(),
                tagRepository.countByAtivoTrue(),
                tagRepository.countByAtivoFalse(),
                subjectRepository.countByAtivoTrue(),
                subjectRepository.countByAtivoFalse(),
                badgeRepository.countByAtivoTrue(),
                badgeRepository.countByAtivoFalse(),
                avatarRepository.countByAtivoTrue(),
                avatarRepository.countByAtivoFalse(),
                bioRepository.countByAtivoTrue(),
                bioRepository.countByAtivoFalse(),
                universityRepository.countByAtivoTrue(),
                universityRepository.countByAtivoFalse(),
                studentRepository.countByAtivoTrue(),
                studentRepository.countByAtivoFalse(),
                adminRepository.count()
        ));
    }
}