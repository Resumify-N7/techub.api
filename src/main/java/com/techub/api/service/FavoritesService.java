package com.techub.api.service;

import com.techub.api.domain.Favorites;
import com.techub.api.domain.Summary;
import com.techub.api.dto.SummaryListResponseDTO;
import com.techub.api.repository.FavoritesRepository;
import com.techub.api.repository.LikesRepository;
import com.techub.api.repository.ReportRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class FavoritesService {

    @Autowired
    private FavoritesRepository favoritesRepository;

    @Autowired
    private LikesRepository likesRepository;

    @Autowired
    private ReportRepository reportRepository;

    @Transactional(readOnly = true)
    public List<SummaryListResponseDTO> getMyFavorites(Long studentId, int limit) {
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

    private SummaryListResponseDTO toListResponse(Summary summary) {
        Integer totalReports = Math.toIntExact(reportRepository.countBySummaryAndReportadoTrue(summary));
        Long totalCurtidas = likesRepository.countBySummary(summary);
        String studentUrl = summary.getStudent().getAvatar() != null ? summary.getStudent().getAvatar().getUrl() : null;
        List<String> tags = summary.getTagLinks().stream()
            .map(link -> link.getTag() != null ? link.getTag().getName() : null)
            .filter(name -> name != null)
            .toList();

        return new SummaryListResponseDTO(
                summary.getStudent().getId(),
                summary.getStudent().getNome(),
                studentUrl,
                summary.getSubject() != null ? summary.getSubject().getId() : null,
                summary.getSubject() != null ? summary.getSubject().getName() : null,
                summary.getId(),
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