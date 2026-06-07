package com.techub.api.service;

import com.techub.api.domain.Summary;
import com.techub.api.dto.FeedDTO;
import com.techub.api.dto.SummaryGetResponseDTO;
import com.techub.api.dto.TagResponseDTO;
import com.techub.api.repository.ReportRepository;
import com.techub.api.repository.SummaryRepository;
import com.techub.api.repository.SummarySpecification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class FeedService {

        private final SummaryRepository summaryRepository;
        private final FollowService followService;
        private LikesService likesService;
        private ReportRepository reportRepository;

        public FeedService(SummaryRepository summaryRepository,
                           FollowService followService,
                           LikesService likesService,
                           ReportRepository reportRepository) {
            this.summaryRepository = summaryRepository;
            this.followService = followService;
            this.reportRepository = reportRepository;
            this.likesService = likesService;
        }

        public FeedDTO getFeed(Long studentId, int page, int size) {

            List<Long> followingUsers = followService.getFollowingUsers(studentId);

            if (followingUsers.isEmpty()) {
                return new FeedDTO(List.of(), page, size, 0);
            }

            Pageable pageable = PageRequest.of(page, size);

            Page<Summary> summaries = summaryRepository.findFeedSummaries(
                    followingUsers,
                    pageable
            );

            return new FeedDTO(
                    summaries.getContent().stream().map(this::toResponse).toList(),
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
                summaries.getContent().stream().map(this::toResponse).toList(),
                summaries.getNumber(),
                summaries.getSize(),
                summaries.getTotalElements()
        );
    }

        private SummaryGetResponseDTO toResponse(Summary summary) {

                Long totalCurtidas = null;

                Integer totalReports = Math.toIntExact(reportRepository.countBySummaryAndReportadoTrue(summary));

                if (Boolean.TRUE.equals(summary.getAtivo()) && Boolean.TRUE.equals(summary.getPublico())) {
                    totalCurtidas = likesService.contarCurtidas(summary);
                }
                String studentUrl = summary.getStudent().getAvatar() != null ? summary.getStudent().getAvatar().getUrl() : null;
                var tags = summary.getTagLinks()
                        .stream()
                        .map(link -> {
                            var tag = link.getTag();
                            if (tag == null) return null;

                            return new TagResponseDTO(
                                    tag.getId(),
                                    tag.getName()
                            );
                        })
                        .filter(Objects::nonNull)
                        .toList();

                return new SummaryGetResponseDTO(summary.getId(),
                        summary.getStudent().getId(),
                        summary.getStudent().getNome(),
                        summary.getStudent().getAvatar().getUrl(),
                        summary.getSubject() != null ? summary.getSubject().getId() : null,
                        summary.getSubject() != null ? summary.getSubject().getName() : null,
                        summary.getTitulo(),
                        summary.getConteudo(),
                        totalReports,
                        summary.getPublico(),
                        summary.getAtivo(),
                        totalCurtidas,
                        tags
                );
        }
}

