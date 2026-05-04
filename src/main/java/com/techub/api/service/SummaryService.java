package com.techub.api.service;

import com.techub.api.domain.Student;
import com.techub.api.domain.Summary;
import com.techub.api.dto.SummaryCreateRequestDTO;
import com.techub.api.dto.SummaryCreateResponseDTO;
import com.techub.api.dto.SummaryGetResponseDTO;
import com.techub.api.dto.SummaryUpdateRequestDTO;
import com.techub.api.repository.StudentRepository;
import com.techub.api.repository.SummaryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SummaryService {

    @Autowired
    private SummaryRepository summaryRepository;

    @Autowired
    private StudentRepository studentRepository;

    public SummaryCreateResponseDTO saveSummary(SummaryCreateRequestDTO dto, Long id) {

        if (dto.titulo() == null || dto.titulo().isEmpty()) {
            throw new IllegalArgumentException("Título é obrigatório");
        }

        if (dto.conteudo() == null || dto.conteudo().isEmpty()) {
            throw new IllegalArgumentException("Conteudo é obrigatório");
        }

        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Estudante não encontrado!"));

        Student studentId = studentRepository.getReferenceById(student.getId());

        Summary summary = new Summary();
        summary.setTitulo(dto.titulo());
        summary.setConteudo(dto.conteudo());
        summary.setStudent(studentId);

        student.getSummaries().add(summary);

        try {
            summaryRepository.save(summary);
            return new SummaryCreateResponseDTO(studentId.getId(), summary.getTitulo(), summary.getConteudo());
        } catch (Exception e) {
            throw new RuntimeException("Erro ao salvar resumo");
        }
    }

    public List<SummaryGetResponseDTO> getAll() {
        return summaryRepository.findAll()
                .stream()
                .map(summary -> new SummaryGetResponseDTO(
                    summary.getStudent().getId(),
                    summary.getId(),
                    summary.getTitulo(),
                    summary.getConteudo()
                ))
                .toList();
    }


    public SummaryGetResponseDTO getById(Long id) {
        Summary summary = summaryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Resumo não encontrado"));
        return new SummaryGetResponseDTO(summary.getStudent().getId(), summary.getId(),  summary.getTitulo(), summary.getConteudo());
    }

    public SummaryGetResponseDTO update(Long id, SummaryUpdateRequestDTO dto) {
        Summary existing = summaryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Resumo não encontrado"));

        existing.setTitulo(dto.titulo());
        existing.setConteudo(dto.conteudo());

        summaryRepository.save(existing);
        return new SummaryGetResponseDTO(existing.getStudent().getId(), existing.getId(), existing.getTitulo(), existing.getConteudo());
    }

    public void delete(Long id) {
        Summary existing = summaryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Resumo não encontrado"));

        summaryRepository.delete(existing);
    }
}