package com.techub.api.service;

import com.techub.api.domain.Student;
import com.techub.api.domain.Summary;
import com.techub.api.dto.*;
import com.techub.api.domain.Course;
import com.techub.api.repository.CourseRepository;
import com.techub.api.repository.StudentRepository;
import com.techub.api.repository.SummaryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SummaryService {

    @Autowired
    private SummaryRepository summaryRepository;
    private final CourseRepository courseRepository;
    private final StudentRepository studentRepository;

    public SummaryService(SummaryRepository summaryRepository,
                          CourseRepository courseRepository,
                          StudentRepository studentRepository) {
        this.summaryRepository = summaryRepository;
        this.courseRepository = courseRepository;
        this.studentRepository = studentRepository;
    }

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

        Course course = courseRepository.findTopByOrderByIdAsc()
            .orElseThrow(() -> new RuntimeException("Curso DSM não encontrado"));
        summary.setCourse(course);

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
                    summary.getConteudo(),
                    summary.getReports(),
                    summary.getAtivo()
                ))
                .toList();
    }

    public List<SummaryGetResponseDTO> findByAtivoTrue() {

        return summaryRepository.findByAtivoTrue()
                .stream()
                .map(summary -> new SummaryGetResponseDTO(
                        summary.getStudent().getId(),
                        summary.getId(),
                        summary.getTitulo(),
                        summary.getConteudo(),
                        summary.getReports(),
                        summary.getAtivo()
                ))
                .toList();
    }

    public List<SummaryGetResponseDTO> findByAtivoFalse() {

        return summaryRepository.findByAtivoFalse()
                .stream()
                .map(summary -> new SummaryGetResponseDTO(
                        summary.getStudent().getId(),
                        summary.getId(),
                        summary.getTitulo(),
                        summary.getConteudo(),
                        summary.getReports(),
                        summary.getAtivo()
                ))
                .toList();
    }

    public SummaryGetResponseDTO getById(Long id) {
        Summary summary = summaryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Resumo não encontrado"));
        return new SummaryGetResponseDTO(summary.getStudent().getId(), summary.getId(),  summary.getTitulo(), summary.getConteudo(), summary.getReports(),  summary.getAtivo());
    }

    public SummaryGetResponseDTO update(Long id, SummaryUpdateRequestDTO dto) {
        Summary existing = summaryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Resumo não encontrado"));

        existing.setTitulo(dto.titulo());
        existing.setConteudo(dto.conteudo());

        summaryRepository.save(existing);
        return new SummaryGetResponseDTO(existing.getStudent().getId(), existing.getId(), existing.getTitulo(), existing.getConteudo(), existing.getReports(),  existing.getAtivo());
    }

    public void reportar(Long id){
        Summary summary = summaryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Não foi possivel encontrar o resumo!"));

        summary.setReports(summary.getReports() + 1);
        summaryRepository.save((summary));
    }

    public void atualizar_status(Long id){
        Summary summary = summaryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Não foi possivel encontrar o resumo!"));

        summary.setAtivo(!summary.getAtivo());
        summaryRepository.save(summary);
    }

    public void delete(Long id) {
        Summary existing = summaryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Resumo não encontrado"));

        summaryRepository.delete(existing);
    }
}