package com.techub.api.service;

import com.techub.api.domain.Likes;
import com.techub.api.domain.Student;
import com.techub.api.domain.Summary;
import com.techub.api.dto.SummaryListResponseDTO;
import com.techub.api.repository.LikesRepository;
import com.techub.api.repository.StudentRepository;
import com.techub.api.repository.SummaryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class LikesService {

    @Autowired
    private LikesRepository likesRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private SummaryRepository summaryRepository;

    public String curtir(Long summaryId, Long studentId) {

        // Busca o resumo no banco, lança erro se não existir
        Summary summary = summaryRepository.findById(summaryId)
                .orElseThrow(() -> new RuntimeException("Resumo não encontrado"));

        // Busca o aluno no banco, lança erro se não existir
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Aluno não encontrado"));

        // Verifica se o aluno já curtiu esse resumo
        // se já curtiu, remove a curtida (toggle)
        var jaGostou = likesRepository.findByStudentAndSummary(student, summary);

        if (jaGostou.isPresent()) {
            // já curtiu → descurte
            likesRepository.delete(jaGostou.get());
            return "Curtida removida";
        }

        // ainda não curtiu → cria a curtida
        Likes like = new Likes();
        like.setStudent(student);
        like.setSummary(summary);
        likesRepository.save(like);

        return "Resumo curtido com sucesso";
    }

    public long contarCurtidas(Long summaryId) {
        // Busca o resumo e retorna o total de curtidas
        Summary summary = summaryRepository.findById(summaryId)
                .orElseThrow(() -> new RuntimeException("Resumo não encontrado"));
        return likesRepository.countBySummary(summary);
    }

    public long contarCurtidas(Summary summary) {
        return likesRepository.countBySummary(summary);
    }

    public List<SummaryListResponseDTO> getRanking(int limit) {
        // Busca os resumos ordenados por curtidas
        List<Object[]> resultado = likesRepository.findRanking();

        int pageSize = Math.max(1, limit);
        return resultado.stream()
                .limit(pageSize)
            .map(this::toRankingResponse)
                .toList();
    }

        private SummaryListResponseDTO toRankingResponse(Object[] linha) {
        Long summaryId = ((Number) linha[0]).longValue();
        Summary summary = summaryRepository.findById(summaryId)
            .orElseThrow(() -> new RuntimeException("Resumo não encontrado"));

        Long subjectId = summary.getSubject() != null ? summary.getSubject().getId() : null;
        String subjectNome = summary.getSubject() != null ? summary.getSubject().getName() : null;

        return new SummaryListResponseDTO(
            ((Number) linha[1]).longValue(),
            (String) linha[2],
            (String) linha[3],
            subjectId,
            subjectNome,
            summaryId,
            (String) linha[4],
            (String) linha[5],
            linha[6] == null ? null : ((Number) linha[6]).intValue(),
            (Boolean) linha[7],
            (Boolean) linha[8],
            ((Number) linha[9]).longValue()
        );
        }
}