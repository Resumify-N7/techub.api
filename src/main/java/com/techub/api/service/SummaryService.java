package com.techub.api.service;

import com.techub.api.domain.Course;
import com.techub.api.domain.Student;
import com.techub.api.domain.Summary;
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

    public Summary saveSummary(Summary summary) {

        if (summary.getTitulo() == null || summary.getTitulo().isEmpty()) {
            throw new IllegalArgumentException("Título é obrigatório");
        }

        // Busca o course real no banco
        if (summary.getCourse() != null && summary.getCourse().getId() != null) {
            Course course = courseRepository.findById(summary.getCourse().getId())
                    .orElseThrow(() -> new RuntimeException("Curso não encontrado"));
            summary.setCourse(course);
        }

        // Busca o student real no banco
        if (summary.getStudent() != null && summary.getStudent().getId() != null) {
            Student student = studentRepository.findById(summary.getStudent().getId())
                    .orElseThrow(() -> new RuntimeException("Student não encontrado"));
            summary.setStudent(student);
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


    public Summary alternarVisibilidade(Long id, Long studentId) {
        Summary summary = getById(id);

        if (!summary.getStudent().getId().equals(studentId)) {
            throw new RuntimeException("Você não tem permissão para alterar este resumo");
        }

        summary.setPublico(!summary.isPublico());

        return summaryRepository.save(summary);
    }
}