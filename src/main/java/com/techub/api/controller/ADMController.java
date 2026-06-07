package com.techub.api.controller;

import com.techub.api.domain.ADM;
import com.techub.api.dto.ADMCreateRequestDTO;
import com.techub.api.dto.ADMGetResponseDTO;
import com.techub.api.dto.DashboardStatsDTO;
import com.techub.api.repository.*;
import com.techub.api.service.ADMService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/adm")
public class ADMController {

    @Autowired
    private ADMService admService;

    @GetMapping
    public List<ADM> listar(@RequestParam(defaultValue = "20") int limit){
        return admService.listar_adm(limit);
    }

    @RestController
    @RequestMapping("/admin/stats")
    public class AdminStatsController {

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

        public AdminStatsController(
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
                ADMRepository adminRepository) {

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
}
