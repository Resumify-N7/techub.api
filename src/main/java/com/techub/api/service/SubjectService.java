package com.techub.api.service;

import com.techub.api.domain.Course;
import com.techub.api.domain.Subject;
import com.techub.api.dto.SubjectListResponseDTO;
import com.techub.api.repository.CourseRepository;
import com.techub.api.repository.SubjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SubjectService {

    @Autowired
    private SubjectRepository subjectRepository;

    @Autowired
    private CourseRepository courseRepository;

    public Subject criar(Subject subject) {
        validarSubject(subject, null);

        subject.setAtivo(true);
        return subjectRepository.save(subject);
    }

    @Transactional(readOnly = true)
    public List<SubjectListResponseDTO> listar(int limit) {
        int pageSize = Math.max(1, limit);
        return subjectRepository.findActive(PageRequest.of(0, pageSize)).getContent().stream()
                .map(this::toListResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<SubjectListResponseDTO> listarDesativados(int limit) {
        int pageSize = Math.max(1, limit);
        return subjectRepository.findInactive(PageRequest.of(0, pageSize)).getContent().stream()
                .map(this::toListResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public Subject buscarPorId(Long id) {
        return subjectRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Matéria não encontrada"));
    }

    public Subject atualizar(Long id, Subject payload) {
        Subject existing = subjectRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Matéria não encontrada"));

        validarSubject(payload, id);

        if (payload.getName() != null && !payload.getName().isBlank()) {
            existing.setName(payload.getName());
        }

        if (payload.getSemestre() != null) {
            existing.setSemestre(payload.getSemestre());
        }

        if (payload.getCourse() != null && payload.getCourse().getId() != null) {
            existing.setCourse(resolverCurso(payload.getCourse().getId()));
        }

        return subjectRepository.save(existing);
    }

    public List<SubjectListResponseDTO> listarPorCurso(Long courseId, int limit) {
        Course course = resolverCurso(courseId);
        int pageSize = Math.max(1, limit);
        return subjectRepository.findByCourseAndAtivoTrue(course).stream()
                .limit(pageSize)
                .map(this::toListResponse)
                .toList();
    }

    public List<SubjectListResponseDTO> listarPorCursoESemestre(Long courseId, Integer semestre, int limit) {
        Course course = resolverCurso(courseId);
        int pageSize = Math.max(1, limit);
        return subjectRepository.findByCourseAndSemestreAndAtivoTrue(course, semestre, PageRequest.of(0, pageSize))
                .getContent()
                .stream()
                .map(this::toListResponse)
                .toList();
    }

    public void atualizar_status(Long id) {
        Subject subject = subjectRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Matéria não encontrada"));

        subject.setAtivo(!Boolean.TRUE.equals(subject.getAtivo()));
        subjectRepository.save(subject);
    }

    public void deletar(Long id) {
        Subject subject = subjectRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Matéria não encontrada"));

        subject.setAtivo(false);
        subjectRepository.save(subject);
    }

    private void validarSubject(Subject subject, Long id) {
        if (subject == null) {
            throw new RuntimeException("Matéria é obrigatória");
        }

        if (subject.getName() == null || subject.getName().isBlank()) {
            throw new RuntimeException("Nome da matéria é obrigatório");
        }

        if (id == null) {
            if (subjectRepository.existsByNameIgnoreCase(subject.getName())) {
                throw new RuntimeException("Nome de matéria já usado");
            }
        } else if (subjectRepository.existsByNameIgnoreCaseAndIdNot(subject.getName(), id)) {
            throw new RuntimeException("Nome de matéria já usado");
        }

        if (subject.getCourse() == null || subject.getCourse().getId() == null) {
            throw new RuntimeException("Curso é obrigatório");
        }

        resolverCurso(subject.getCourse().getId());
    }

    private Course resolverCurso(Long courseId) {
        return courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Curso não encontrado"));
    }

    private SubjectListResponseDTO toListResponse(Subject subject) {
        return new SubjectListResponseDTO(
                subject.getId(),
                subject.getName(),
                subject.getSemestre(),
                subject.getCourse() != null ? subject.getCourse().getId() : null,
                subject.getCourse() != null ? subject.getCourse().getName() : null,
                subject.getAtivo()
        );
    }
}