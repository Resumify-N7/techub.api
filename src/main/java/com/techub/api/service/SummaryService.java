package com.techub.api.service;

import com.techub.api.domain.*;
import com.techub.api.dto.*;
import com.techub.api.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class SummaryService {

    @Autowired
    private SummaryRepository summaryRepository;
    @Autowired
    private LikesService likesService;
    @Autowired
    private ReportRepository reportRepository;
    @Autowired
    private final CourseRepository courseRepository;
    @Autowired
    private final StudentRepository studentRepository;
    @Autowired
    private final UserRepository userRepository;

    public SummaryService(SummaryRepository summaryRepository,
                          CourseRepository courseRepository,
                          StudentRepository studentRepository,
                          UserRepository userRepository) {
        this.userRepository = userRepository;
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

        if (student.getSummaries() == null) {
            student.setSummaries(new java.util.ArrayList<>());
        }
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

    private SummaryGetResponseDTO toResponse(Summary summary, boolean includeLikes) {
        Long totalCurtidas = null;

        Integer totalReports = Math.toIntExact(reportRepository.countBySummaryAndReportadoTrue(summary));

        if (includeLikes && Boolean.TRUE.equals(summary.getAtivo()) && Boolean.TRUE.equals(summary.getPublico())) {
            totalCurtidas = likesService.contarCurtidas(summary);
        }

        return new SummaryGetResponseDTO(
                summary.getStudent().getId(),
                summary.getId(),
                summary.getTitulo(),
                summary.getConteudo(),
                totalReports,
                summary.getPublico(),
                summary.getAtivo(),
                totalCurtidas
        );
    }

    public List<SummaryGetResponseDTO> getAll() {
        return summaryRepository.findAll()
                .stream()
                .map(summary -> toResponse(summary, true))
                .toList();
    }

    public List<SummaryGetResponseDTO> findByAtivoTrue() {

        return summaryRepository.findByAtivoTrue()
                .stream()
                .filter(summary -> summary.getPublico() == true)
            .map(summary -> toResponse(summary, true))
                .toList();
    }

    public List<SummaryGetResponseDTO> findByAtivoFalse() {

        return summaryRepository.findByAtivoFalse()
                .stream()
            .map(summary -> toResponse(summary, false))
                .toList();
    }

    public SummaryGetResponseDTO getById(Long id) {
        Summary summary = summaryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Resumo não encontrado"));
        return toResponse(summary, true);
    }

    public List<SummaryGetResponseDTO> getStudentSummary(Long id){
        Student student = studentRepository.findById(id)
                .orElseGet(() -> {
                    User user = userRepository.findById(id)
                            .orElseThrow(() -> new ResponseStatusException(
                                    org.springframework.http.HttpStatus.NOT_FOUND,
                                    "Usuário não encontrado"
                            ));

                    Student linkedStudent = user.getStudent();
                    if (linkedStudent == null) {
                        throw new ResponseStatusException(
                                org.springframework.http.HttpStatus.NOT_FOUND,
                                "Perfil de estudante não vinculado ao usuário"
                        );
                    }

                    return linkedStudent;
                });

        return summaryRepository.findByStudentId(student.getId())
                .stream()
                .map(summary -> toResponse(summary, true))
                .toList();
    }

    public SummaryGetResponseDTO update(Long id, SummaryUpdateRequestDTO dto) {
        Summary existing = summaryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Resumo não encontrado"));

        existing.setTitulo(dto.titulo());
        existing.setConteudo(dto.conteudo());

        summaryRepository.save(existing);
        return toResponse(existing, true);
    }

    public void reportar(Long id, Long studentId){
        Summary summary = summaryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Não foi possivel encontrar o resumo!"));

        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Aluno não encontrado"));

        if (reportRepository.existsByStudentAndSummary(student, summary)) {
            throw new RuntimeException("Você já reportou este resumo");
        }

        Report report = new Report();
        report.setStudent(student);
        report.setSummary(summary);
        report.setReportado(true);

        reportRepository.save(report);

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

        reportRepository.deleteBySummary(existing);
        summaryRepository.delete(existing);
    }

    public void alternarVisibilidade(Long summaryId, Long studentId) {
        Summary summary = summaryRepository.findById(summaryId)
                .orElseThrow(() -> new RuntimeException("Erro ao procurar resumo!"));

        if (!summary.getStudent().getId().equals(studentId)) {
            throw new RuntimeException("Você não tem permissão para alterar este resumo");
        }

        summary.setPublico(!summary.getPublico());

        summaryRepository.save(summary);
    }
}