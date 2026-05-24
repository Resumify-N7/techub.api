package com.techub.api.service;

import com.techub.api.domain.Summary;
import com.techub.api.dto.FeedDTO;
import com.techub.api.dto.SummaryListResponseDTO;
import com.techub.api.repository.SummaryRepository;
import com.techub.api.repository.SummarySpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FeedService {

        private final SummaryRepository summaryRepository;
        private final FollowService followService;

        public FeedService(SummaryRepository summaryRepository,
                           FollowService followService) {
            this.summaryRepository = summaryRepository;
            this.followService = followService;
        }

        public FeedDTO getFeed(Long studentId, int page, int size) {

            List<Long> followingUsers = followService.getFollowingUsers(studentId);
            List<Long> followingCourses = followService.getFollowingCourses(studentId);

            if (followingUsers.isEmpty() && followingCourses.isEmpty()) {
                return new FeedDTO(List.of(), page, size, 0);
            }

            Pageable pageable = PageRequest.of(page, size);

            Page<Summary> summaries = summaryRepository.findFeedSummaries(
                    followingUsers,
                    followingCourses,
                    pageable
            );

            return new FeedDTO(
                    summaries.getContent().stream().map(this::toListResponse).toList(),
                    summaries.getNumber(),
                    summaries.getSize(),
                    summaries.getTotalElements()
            );
        }

    public FeedDTO getFilteredFeed(Long universityId, Long courseId,
                                   Long tagId, Integer semestre,
                                   int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        Specification<Summary> spec = ((Specification<Summary>) (root, query, cb) -> null)
                .and(SummarySpecification.byCourseId(courseId))
                .and(SummarySpecification.byUniversityId(universityId))
                .and(SummarySpecification.byTagId(tagId))
                .and(SummarySpecification.bySemestre(semestre));

        Page<Summary> summaries = summaryRepository.findAll(spec, pageable);

        return new FeedDTO(
                                summaries.getContent().stream().map(this::toListResponse).toList(),
                summaries.getNumber(),
                summaries.getSize(),
                summaries.getTotalElements()
        );
    }

        private SummaryListResponseDTO toListResponse(Summary summary) {
                String studentUrl = summary.getStudent().getAvatar() != null ? summary.getStudent().getAvatar().getUrl() : null;

                return new SummaryListResponseDTO(
                                summary.getStudent().getId(),
                                summary.getStudent().getNome(),
                                studentUrl,
                                summary.getId(),
                                summary.getTitulo(),
                                summary.getConteudo(),
                                summary.getReports(),
                                summary.getPublico(),
                                summary.getAtivo(),
                                null
                );
        }
}

