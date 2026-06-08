package com.techub.api.service;

import com.techub.api.domain.Favorites;
import com.techub.api.domain.Summary;
import com.techub.api.dto.SummaryGetResponseDTO;
import com.techub.api.dto.TagResponseDTO;
import com.techub.api.repository.FavoritesRepository;
import com.techub.api.repository.LikesRepository;
import com.techub.api.repository.ReportRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
public class FavoritesService {

    @Autowired
    private FavoritesRepository favoritesRepository;

    @Autowired
    private LikesRepository likesRepository;

    @Autowired
    private ReportRepository reportRepository;

    @Transactional(readOnly = true)
    public List<SummaryGetResponseDTO> getMyFavorites(Long studentId, int limit) {
        int pageSize = Math.max(1, limit);

        return favoritesRepository.findByStudentIdAndAtivoTrueOrderByIdDesc(studentId, PageRequest.of(0, pageSize))
                .getContent()
                .stream()
                .map(Favorites::getSummary)
                .filter(summary -> Boolean.TRUE.equals(summary.getAtivo()))
                .map(this::toListResponse)
                .toList();
    }

    public void syncFavoriteFromLike(Summary summary) {
        Favorites favorite = favoritesRepository.findByStudentAndSummary(summary.getStudent(), summary)
                .orElseGet(Favorites::new);

        favorite.setStudent(summary.getStudent());
        favorite.setSummary(summary);
        favorite.setAtivo(true);
        favoritesRepository.save(favorite);
    }

    public void removeFavoriteFromLike(Summary summary) {
        favoritesRepository.findByStudentAndSummary(summary.getStudent(), summary)
                .ifPresent(favoritesRepository::delete);
    }

    private SummaryGetResponseDTO toListResponse(Summary summary) {
        Integer totalReports = Math.toIntExact(reportRepository.countBySummaryAndReportadoTrue(summary));
        Long totalCurtidas = likesRepository.countBySummary(summary);
        String studentUrl = summary.getStudent().getAvatar() == null ? "/avatares/default.svg": summary.getStudent().getAvatar().getUrl();
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

        return new SummaryGetResponseDTO(
                summary.getId(),
                summary.getStudent().getId(),
                summary.getStudent().getNome(),
                studentUrl,
                summary.getSubject() != null ? summary.getSubject().getId() : null,
                summary.getSubject() != null ? summary.getSubject().getName() : null,
                summary.getTitulo(),
                summary.getConteudo(),
                totalReports,
                summary.getPublico(),
                summary.getAtivo(),
                totalCurtidas,
                tags,
                summary.getBadge() != null
                        ? new SummaryGetResponseDTO.BadgeDTO(
                                summary.getBadge().getId(),
                                summary.getBadge().getName(),
                                summary.getBadge().getDescription())
                        : null
        );
    }
}