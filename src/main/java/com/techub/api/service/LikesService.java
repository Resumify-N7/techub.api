package com.techub.api.service;

import com.techub.api.domain.Badge;
import com.techub.api.domain.Favorites;
import com.techub.api.domain.Likes;
import com.techub.api.domain.Student;
import com.techub.api.domain.Summary;
import com.techub.api.dto.SummaryGetResponseDTO;
import com.techub.api.dto.TagResponseDTO;
import com.techub.api.repository.BadgeRepository;
import com.techub.api.repository.FavoritesRepository;
import com.techub.api.repository.LikesRepository;
import com.techub.api.repository.StudentRepository;
import com.techub.api.repository.SummaryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Objects;

@Service
public class LikesService {

    @Autowired
    private LikesRepository likesRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private SummaryRepository summaryRepository;

    @Autowired
    private BadgeRepository badgeRepository;

    @Autowired
    private FavoritesRepository favoritesRepository;

    @Transactional
    public String curtir(Long summaryId, Long studentId) {

        Summary summary = summaryRepository.findById(summaryId)
                .orElseThrow(() -> new RuntimeException("Resumo não encontrado"));

        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Aluno não encontrado"));

        var jaGostou = likesRepository.findByStudentAndSummary(student, summary);

        if (jaGostou.isPresent()) {
            likesRepository.delete(jaGostou.get());
            removeFavorite(student, summary);
            return "Curtida removida";
        }

        Likes like = new Likes();
        like.setStudent(student);
        like.setSummary(summary);
        likesRepository.save(like);

        syncFavorite(student, summary);

        concederBadgeSeNecessario(summary);

        return "Resumo curtido com sucesso";
    }

    public long contarCurtidas(Long summaryId) {
        Summary summary = summaryRepository.findById(summaryId)
                .orElseThrow(() -> new RuntimeException("Resumo não encontrado"));
        return likesRepository.countBySummary(summary);
    }

    public long contarCurtidas(Summary summary) {
        return likesRepository.countBySummary(summary);
    }

    public List<SummaryGetResponseDTO> getRanking(int limit) {
        List<Object[]> resultado = likesRepository.findRanking();

        int pageSize = Math.max(1, limit);
        return resultado.stream()
                .limit(pageSize)
            .map(this::toRankingResponse)
                .toList();
    }

        private SummaryGetResponseDTO toRankingResponse(Object[] linha) {
            Long summaryId = ((Number) linha[0]).longValue();
            Summary summary = summaryRepository.findById(summaryId)
                .orElseThrow(() -> new RuntimeException("Resumo não encontrado"));

            Long subjectId = summary.getSubject() != null ? summary.getSubject().getId() : null;
            String subjectNome = summary.getSubject() != null ? summary.getSubject().getName() : null;
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
                    summaryId,
                    ((Number) linha[1]).longValue(),
                    (String) linha[2],
                    (String) linha[3],
                    subjectId,
                    subjectNome,
                    (String) linha[4],
                    (String) linha[5],
                    linha[6] == null ? null : ((Number) linha[6]).intValue(),
                    (Boolean) linha[7],
                    (Boolean) linha[8],
                    ((Number) linha[9]).longValue(),
                    tags,
                    summary.getBadge() != null
                            ? new SummaryGetResponseDTO.BadgeDTO(
                                    summary.getBadge().getId(),
                                    summary.getBadge().getName(),
                                    summary.getBadge().getDescription())
                            : null
            );
        }

    private void concederBadgeSeNecessario(Summary summary) {
        long totalCurtidas = likesRepository.countBySummary(summary);

        if (totalCurtidas < 50) {
            return;
        }

        if (summary.getBadge() != null) {
            return;
        }

        Badge badge = badgeRepository.findByNameIgnoreCaseAndAtivoTrue("Resumo Popular")
                .orElse(null);

        if (badge == null) {
            return;
        }

        summary.setBadge(badge);
        summaryRepository.save(summary);
    }

    private void syncFavorite(Student student, Summary summary) {
        Favorites favorite = favoritesRepository.findByStudentAndSummary(student, summary)
                .orElseGet(Favorites::new);

        favorite.setStudent(student);
        favorite.setSummary(summary);
        favorite.setAtivo(true);
        favoritesRepository.save(favorite);
    }

    private void removeFavorite(Student student, Summary summary) {
        favoritesRepository.findByStudentAndSummary(student, summary)
                .ifPresent(favoritesRepository::delete);
    }
}