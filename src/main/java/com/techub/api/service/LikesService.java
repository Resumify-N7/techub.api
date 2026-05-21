package com.techub.api.service;

import com.techub.api.domain.Likes;
import com.techub.api.domain.Student;
import com.techub.api.domain.Summary;
import com.techub.api.repository.LikesRepository;
import com.techub.api.repository.StudentRepository;
import com.techub.api.repository.SummaryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.LinkedHashMap;

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

    public List<Map<String, Object>> getRanking() {
        // Busca os resumos ordenados por curtidas
        List<Object[]> resultado = likesRepository.findRanking();

        // Converte para um formato legível na resposta
        List<Map<String, Object>> ranking = new ArrayList<>();

        for (Object[] linha : resultado) {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("resumo", linha[0]);        // o objeto Summary
            item.put("totalCurtidas", linha[1]); // o total de curtidas
            ranking.add(item);
        }

        return ranking;
    }
}