package com.techub.api.service;

import com.techub.api.domain.Summary;
import com.techub.api.repository.SummaryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SummaryService {

    @Autowired
    private SummaryRepository summaryRepository;

    public Summary saveSummary(Summary summary) {

        if (summary.getTitulo() == null || summary.getTitulo().isEmpty()) {
            throw new IllegalArgumentException("Título é obrigatório");
        }

        try {
            return summaryRepository.save(summary);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao salvar resumo");
        }
    }

    public List<Summary> getAll() {
        return summaryRepository.findAll();
    }


    public Summary getById(Long id) {
        return summaryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Resumo não encontrado"));
    }

    public Summary update(Long id, Summary summary) {
        Summary existing = getById(id);

        existing.setTitulo(summary.getTitulo());
        existing.setConteudo(summary.getConteudo());

        return summaryRepository.save(existing);
    }


    public void delete(Long id) {
        Summary existing = getById(id);
        summaryRepository.delete(existing);
    }
}